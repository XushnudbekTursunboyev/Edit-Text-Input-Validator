package uz.orign.edittextvalidatorlib

import android.content.Context
import android.content.res.TypedArray
import android.text.*
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import uz.orign.edittextvalidatorlib.AlertFactory.showPickerDialg
import uz.orign.edittextvalidatorlib.ValidateFormsUtils.isValidEmailAddress
import uz.orign.edittextvalidatorlib.ValidateFormsUtils.isValidPhone
import uz.orign.edittextvalidatorlib.ValidationUtils.parseCurrencyAmount
import uz.orign.edittextvalidatorlib.ValidationUtils.parseCurrencyAmountWithoutDecimal
import java.util.*
import java.util.regex.Pattern


class ValidationEditText : AppCompatEditText, TextWatcher {
    //endregion
    private var mFormatType: ValidationType = ValidationType.defaulttype
    private var customLocale: Locale = Locale.getDefault()

    //region Getters&Setters
    //region flags
    var isAutoValidateEnable = true
    var isShowMessageError = true
    private var mFirstime = true

    //endregion
    //region data
    private var mCurrentString = ""
    var maxMount = 0.0
    var minMount = 0.0
    var regularExpression: String? = null

    //endregion
    //region messages
    var emptyMessage: String? = null
    var errorMessage: String? = null

    //endregion
    //region parameters
    @DrawableRes
    private var drawableOptions: Int = R.drawable.ic_expand_more
    private var options: Array<String?>? = null
    private var mAutoValidate: OnValidationListener? = null

    //endregion
    //region constructors
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, type: ValidationType?) : super(context, attrs) {
        if (type != null) {
            mFormatType = type
        }
        init(context, attrs)
    }

    constructor(context: Context, type: ValidationType?) : super(context) {
        if (type != null) {
            mFormatType = type
        }
        init(context, null)
    }

    //endregion
    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray: TypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ValidationEditText,
                0, 0
            )
            try {
                mFormatType = ValidationType.fromId(typedArray.getInt(R.styleable.ValidationEditText_format, -11))
                isAutoValidateEnable = typedArray.getBoolean(R.styleable.ValidationEditText_autoValidate, false)
                isShowMessageError = typedArray.getBoolean(R.styleable.ValidationEditText_showErrorMessage, false)
                emptyMessage = typedArray.getString(R.styleable.ValidationEditText_errorEmptyMessage)
                errorMessage = typedArray.getString(R.styleable.ValidationEditText_errorMessage)
                regularExpression = typedArray.getString(R.styleable.ValidationEditText_regularExpression)
                minMount = typedArray.getFloat(R.styleable.ValidationEditText_minAmount, 0f).toDouble()
                maxMount = typedArray.getFloat(R.styleable.ValidationEditText_maxAmount, 0f).toDouble()
                drawableOptions = typedArray.getResourceId(R.styleable.ValidationEditText_drawableOptions, R.drawable.ic_expand_more)
                try {
                    val id = typedArray.getResourceId(R.styleable.ValidationEditText_options, 0)
                    if (id != 0) {
                        options = resources.getStringArray(id)
                    }
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
                mFormatType = ValidationType.defaulttype
            } finally {
                typedArray.recycle()
            }
        }
        super.addTextChangedListener(this)
        configureType(mFormatType)
        if (options != null && options!!.size > 0) {
            setPickerOptions(options, null)
        }
    }

    private fun configureType(mFormatType: ValidationType?) {
        if (mFormatType != null) {
            if (!mFormatType.equals(ValidationType.defaulttype)) {
                when (mFormatType) {
                    ValidationType.email -> {
                        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        this.maxLines = 1
                    }
                    ValidationType.numberCurrency, ValidationType.numberCurrencyRounded, ValidationType.number -> {
                        this.maxLines = 1
                        this.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    ValidationType.zipcode -> {
                        this.maxLines = 1
                        this.filters = arrayOf<InputFilter>(LengthFilter(CONST_POSTAL_CODE_SIZE))
                        this.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    text -> {
                        this.maxLines = 1
                        this.inputType = InputType.TYPE_CLASS_TEXT
                    }
                    ValidationType.cellphone -> {
                        this.maxLines = 1
                        this.filters = arrayOf<InputFilter>(LengthFilter(CONST_CELLPHONE_SIZE))
                        this.inputType = InputType.TYPE_CLASS_PHONE
                    }
                    ValidationType.phone -> {
                        this.maxLines = 1
                        this.inputType = InputType.TYPE_CLASS_PHONE
                    }
                    ValidationType.curp -> {
                        this.maxLines = 1
                        this.filters = arrayOf(
                            LengthFilter(CONST_CURP_SIZE),  //longitud
                            AllCaps()
                        ) // input mayusculas
                        this.inputType = InputType.TYPE_CLASS_TEXT
                    }
                    ValidationType.personName -> {
                        this.maxLines = 1
                        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    }
                    ValidationType.password -> {
                        this.maxLines = 1
                        val cache = this.typeface
                        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        this.setTypeface(cache)
                    }
                    ValidationType.date -> {
                        this.maxLines = 1
                        this.inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
                    }
                    else -> {}
                }
            }
        }
    }

    override fun getHint(): CharSequence {
        return if (super.getHint() == null && inputLayoutContainer != null) {
            inputLayoutContainer!!.hint!!
        } else super.getHint()
    }

    val inputLayoutContainer: TextInputLayout?
        get() {
            if (this.parent != null) {
                if (this.parent is TextInputLayout) {
                    return this.parent as TextInputLayout
                } else {
                    if (this.parent.parent != null && this.parent.parent is TextInputLayout) {
                        return this.parent.parent as TextInputLayout
                    }
                }
            }
            return null
        }
    val isValidField: Boolean
        get() = validateEditText(mFormatType, mCurrentString)

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (!mFormatType.equals(ValidationType.defaulttype)) {
            removeTextChangedListener(this)
            val currentString = s.toString()
            when (mFormatType) {
                ValidationType.numberCurrencyRounded -> if (currentString.length == 0) {
                    this@ValidationEditText.setText("")
                    mFirstime = true
                    mCurrentString = ""
                } else if (!currentString.equals(mCurrentString, ignoreCase = true)) {
                    val formatted: String = ValidationUtils.cashFormat(customLocale, parseCurrencyAmountWithoutDecimal(s.toString()))
                    mCurrentString = formatted
                    this@ValidationEditText.setText(formatted)
                    this@ValidationEditText.setSelection(formatted.length)
                }
                ValidationType.numberCurrency -> if (currentString.length == 0) {
                    this@ValidationEditText.setText("")
                    mFirstime = true
                    mCurrentString = ""
                } else if (!currentString.equals(mCurrentString, ignoreCase = true)) {
                    val formatted: String = ValidationUtils.cashFormat(customLocale, parseCurrencyAmount(s.toString()) / 100)
                    mCurrentString = formatted
                    this@ValidationEditText.setText(formatted)
                    this@ValidationEditText.setSelection(formatted.length)
                }
                else -> mCurrentString = currentString
            }
            if (isAutoValidateEnable && !mFirstime) {
                validateEditText(mFormatType, mCurrentString)
            }
            addTextChangedListener(this)
            mFirstime = false
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    private fun validateEditText(mFormatType: ValidationType, currentString: String): Boolean {
        var validField = true
        var errorMessage: String? = null
        if (TextUtils.isEmpty(currentString.trim { it <= ' ' })) {
            errorMessage = if (emptyMessage != null) emptyMessage else context.getString(R.string.msg_empty_edittext)
            validField = false
        } else if (regularExpression != null) {
            val customizePattern: Pattern = Pattern.compile(regularExpression.toString())
            validField = customizePattern.matcher(currentString).matches()
            if (!validField) {
                errorMessage = if (this.errorMessage != null) this.errorMessage else context.getString(R.string.msg_invalid_edittext)
            }
        } else {
            when (mFormatType) {
                ValidationType.email -> {
                    validField = isValidEmailAddress(currentString)
                    if (!validField) {
                        errorMessage = if (this.errorMessage != null) this.errorMessage else context.getString(R.string.msg_invalid_edittext)
                    }
                }
                ValidationType.numberCurrencyRounded, ValidationType.numberCurrency -> {
                    val currentMount: Double = parseCurrencyAmount(currentString) / 100
                    if (currentMount == 0.0 || currentMount < minMount || currentMount > maxMount && maxMount > 0) {
                        validField = false
                        errorMessage = context.getString(
                            R.string.msg_invalid_edittext_currency, ValidationUtils.cashFormat(
                                customLocale,
                                minMount
                            ), ValidationUtils.cashFormat(customLocale, maxMount)
                        )
                    }
                }
                ValidationType.phone, ValidationType.cellphone -> {
                    validField = isValidPhone(currentString)
                    if (!validField) {
                        errorMessage = if (this.errorMessage != null) this.errorMessage else context.getString(R.string.msg_invalid_edittext)
                    }
                }
                ValidationType.curp -> {
                    validField = currentString.length == 18
                    if (!validField) {
                        errorMessage = if (this.errorMessage != null) this.errorMessage else context.getString(R.string.msg_invalid_edittext)
                    }
                }
                else -> {}
            }
        }
        if (mAutoValidate != null) {
            if (validField) {
                mAutoValidate!!.onValidEditText(this@ValidationEditText, mCurrentString)
            } else {
                mAutoValidate!!.onInvalidEditText(this@ValidationEditText)
            }
        }
        if (isShowMessageError) {
            setErrorTextInputLayout(errorMessage)
        }
        return validField
    }

    private fun setErrorTextInputLayout(errorMessage: String?) {
        if (inputLayoutContainer != null) {
            inputLayoutContainer!!.error = errorMessage
        } else {
            super.setError(errorMessage)
        }
    }

    private fun setErrorTextInputLayout(errorMessage: CharSequence) {
        if (inputLayoutContainer != null) {
            inputLayoutContainer!!.error = errorMessage
        } else {
            super.setError(errorMessage)
        }
    }

    override fun setError(error: CharSequence) {
        setErrorTextInputLayout(error)
    }

    fun setPickerOptions(options: Array<String?>?, listener: OptionsListener?) {
        if (options == null) {
            return
        }
        enablePickerMode({
            showPickerDialg(
                context,
                hint.toString(),
                options
            ) { dialog, which ->
                setText(options[which])
                listener?.onOptionSelected(this@ValidationEditText, options[which])
            }
        })
    }

    fun setDrawableOptions(drawableOptions: Int) {
        this.drawableOptions = drawableOptions
        setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableOptions, 0)
    }

    fun removeDrawableOptions() {
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }

    fun enablePickerMode(listener: OnClickListener?) {
        setOnClickListener(listener)
        isLongClickable = false
        isClickable = true
        isFocusable = false
        inputType = InputType.TYPE_NULL
        isCursorVisible = false
        setDrawableOptions(drawableOptions)
    }

    fun disablePickerMode() {
        setOnClickListener(null)
        isLongClickable = true
        isClickable = false
        isFocusable = true
        isCursorVisible = true
        removeDrawableOptions()
        configureType(mFormatType)
    }

    interface OptionsListener {
        fun onOptionSelected(editText: ValidationEditText?, option: String?)
    }

    interface OnValidationListener {
        fun onValidEditText(editText: ValidationEditText?, text: String?)
        fun onInvalidEditText(editText: ValidationEditText?)
    }

    fun setOnValidationListener(mAutoValidate: OnValidationListener?) {
        this.mAutoValidate = mAutoValidate
    }

    fun getCustomLocale(): Locale {
        return customLocale
    }

    fun setCustomLocale(customLocale: Locale) {
        this.customLocale = customLocale
    }

    val amount: Double
        get() = when (mFormatType) {
            ValidationType.numberCurrency, ValidationType.numberCurrencyRounded -> parseCurrencyAmount(this.text.toString())
            else -> 0.0
        }
    var formatType: ValidationType
        get() = mFormatType
        set(mFormatType) {
            this.mFormatType = mFormatType
            configureType(mFormatType)
        }

    fun getDrawableOptions(): Int {
        return drawableOptions
    } //endregion

    companion object {
        //region Constants
        private const val CONST_POSTAL_CODE_SIZE = 5 //MX
        private const val CONST_CELLPHONE_SIZE = 10 //MX
        private const val CONST_CURP_SIZE = 18
    }
}