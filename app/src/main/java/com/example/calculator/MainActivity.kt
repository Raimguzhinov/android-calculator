package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.abs

private var firstOperand = ""
private var secondOperand = ""
private var eqOperator = ""
private var eqResult = 0.0
private var inputStateStart = false
private var inputStateStop = false

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val result: TextView = findViewById(R.id.tvResult) as TextView
        val formula: TextView = findViewById(R.id.tvFormula) as TextView

        val buttons = arrayOf(
            findViewById<com.google.android.material.button.MaterialButton>(R.id.clear),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.mod),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.abs),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.equal),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.point),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.add),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.sub),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.mul),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.div),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.one),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.two),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.three),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.four),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.five),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.six),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.seven),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.eight),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.nine),
            findViewById<com.google.android.material.button.MaterialButton>(R.id.zero)
        )

        for (button in buttons) {
            button.setOnClickListener {
                button.text.toString()
                when {
                    button.text.matches(Regex("[0-9]")) -> {
                        if (inputStateStart && inputStateStop) {
                            firstOperand = ""
                            secondOperand = ""
                            eqOperator = ""
                            formula.text = ""
                            result.text = "0"
                        }
                        inputStateStop = false
                        if (eqOperator.isEmpty()) {
                            firstOperand += button.text
                            result.text = firstOperand
                        } else {
                            secondOperand += button.text
                            result.text = secondOperand
                        }
                        inputStateStart = true
                        findViewById<com.google.android.material.button.MaterialButton>(R.id.clear).text = "C"
                    }
                    button.text.matches(Regex("[+–×÷]")) -> {
                        secondOperand = ""
                        if (firstOperand.isEmpty()) {
                            firstOperand = "0"
                        }
                        if (result.text.toString().isNotEmpty()) {
                            eqOperator = button.text.toString()
                            result.text = "0"
                        }
                    }
                    button.text == "=" -> {
                        if (secondOperand.isNotEmpty() && eqOperator.isNotEmpty()) {
                            if(!secondOperand.contains("-")) {
                                formula.text = "$firstOperand$eqOperator$secondOperand"
                            } else {
                                formula.text = "$firstOperand$eqOperator($secondOperand)"
                            }
                            eqResult = evaluateExpression(firstOperand, secondOperand, eqOperator)
                            if (eqResult % 1.0 == 0.0) {
                                firstOperand = eqResult.toInt().toString()
                            } else {
                                firstOperand = eqResult.toString()
                            }
                            result.text = firstOperand
                        }
                        inputStateStop = true
                    }
                    button.text == "." -> {
                        if (eqOperator.isEmpty() && !firstOperand.contains(".")) {
                            if (firstOperand.isEmpty()) firstOperand += "0${button.text}"
                            else firstOperand += button.text
                            result.text = firstOperand
                        } else if (!secondOperand.contains(".")) {
                            if (secondOperand.isEmpty()) secondOperand += "0${button.text}"
                            else secondOperand += button.text
                            result.text = secondOperand
                        }
                    }
                    button.text == "A/C" -> {
                        firstOperand = ""
                        secondOperand = ""
                        eqOperator = ""
                        formula.text = ""
                        result.text = "0"
                    }
                    button.text == "C" -> {
                        firstOperand = ""
                        secondOperand = ""
                        eqOperator = ""
                        formula.text = ""
                        result.text = "0"
                        button.text = "A/C"
                    }
                    button.text == "±" -> {
                        if (eqOperator.isEmpty()) {
                            if(!firstOperand.contains("-")) {
                                if (firstOperand.isEmpty()) firstOperand += "-0"
                                else {
                                    firstOperand = "-$firstOperand"
                                }
                            } else {
                                val num = firstOperand.toDouble()
                                if (firstOperand.isEmpty()) firstOperand = "0"
                                else {
                                    firstOperand = abs(num).toString()
                                }
                            }
                            result.text = firstOperand
                        }
                        else {
                            if(!secondOperand.contains("-")) {
                                if (secondOperand.isEmpty()) secondOperand += "-0"
                                else {
                                    secondOperand = "-$secondOperand"
                                }
                            } else {
                                val num = secondOperand.toDouble()
                                if (secondOperand.isEmpty()) secondOperand = "0"
                                else {
                                    secondOperand = abs(num).toString()
                                }
                            }
                            result.text = secondOperand
                        }
                    }
                    button.text == "%" -> {
                        if (eqOperator.isEmpty() && firstOperand.isNotEmpty()) {
                            val num = firstOperand.toDouble()
                            firstOperand = (num / 100).toString()
                            result.text = firstOperand
                        } else if (secondOperand.isNotEmpty()) {
                            if (eqOperator.matches((Regex("[×÷]")))) {
                                val num = secondOperand.toDouble()
                                secondOperand = (num / 100).toString()
                            } else {
                                val num1 = firstOperand.toDouble()
                                val num2 = secondOperand.toDouble()
                                secondOperand = (num1 * num2 / 100).toString()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun evaluateExpression(firstOperand: String, secondOperand: String, operator: String): Double {
        val a = firstOperand.toDouble()
        val b = secondOperand.toDouble()
        return when (operator) {
            "+" -> (a + b)
            "–" -> (a - b)
            "×" -> (a * b)
            "÷" -> (a / b)
            else -> Double.NaN
        }
    }

}