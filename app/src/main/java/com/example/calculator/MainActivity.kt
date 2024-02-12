package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.abs
import java.util.Stack

private var firstOperand = ""
private var secondOperand = ""
private var eqOperator = ""
private var cache = ""
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
                            cache = ""
                        }
                        inputStateStop = false
                        if (eqOperator.isEmpty()) {
                            firstOperand += button.text
                            result.text = firstOperand
                            cache += firstOperand
                        } else {
                            secondOperand += button.text
                            result.text = secondOperand
                            cache += eqOperator
                            cache += secondOperand
                        }
                        inputStateStart = true
                        findViewById<com.google.android.material.button.MaterialButton>(R.id.clear).text = "C"
                    }
                    button.text.matches(Regex("[+–×÷]")) -> {
                        inputStateStop = false
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
                            formula.text = cache
                            eqResult = evaluateExpression().toDouble()
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
                        cache = ""
                    }
                    button.text == "C" -> {
                        firstOperand = ""
                        secondOperand = ""
                        eqOperator = ""
                        formula.text = ""
                        result.text = "0"
                        button.text = "A/C"
                        cache = ""
                    }
                    button.text == "±" -> {
                        cache = cache.dropLast(1)
                        if (eqOperator.isEmpty()) {
                            if(!firstOperand.contains("-")) {
                                if (firstOperand.isEmpty()) firstOperand += "–0"
                                else {
                                    firstOperand = "–$firstOperand"
                                }
                            } else {
                                val num = firstOperand.toDouble()
                                if (firstOperand.isEmpty()) firstOperand = "0"
                                else {
                                    firstOperand = abs(num).toString()
                                }
                            }
                            result.text = firstOperand
                            cache += firstOperand
                        }
                        else {
                            if(!secondOperand.contains("–")) {
                                if (secondOperand.isEmpty()) secondOperand += "–0"
                                else {
                                    secondOperand = "–$secondOperand"
                                    cache += "($secondOperand)"
                                }
                            } else {
                                val num = secondOperand.toDouble()
                                if (secondOperand.isEmpty()) secondOperand = "0"
                                else {
                                    secondOperand = abs(num).toString()
                                    cache += secondOperand
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

    private var postfixExpr: String = ""
    private var position = 0
    private val priorityMap: Map<Char, Int> = mapOf(
        '(' to 0,
        '+' to 1,
        '-' to 1,
        '×' to 2,
        '÷' to 2,
        '~' to 3
    )

    private fun checkPreviousOperator() {
        if(cache.takeLast(1).matches(Regex("[+\\-÷×]"))){
            cache = cache.dropLast(1)
        }
    }


    private fun readNumber(buffer: String) : String {
        var number: String = ""
        while(position < buffer.length && (buffer[position].isDigit() || buffer[position] == '.')) {
            number += buffer[position]
            position++
        }
        --position
        return number
    }

    private fun execute(op: Char, first: Double, second: Double) : Double {
        return when(op) {
            '+' -> (first + second)
            '-' -> (first - second)
            '×' -> (first * second)
            '÷' -> (first / second)
            else -> Double.NaN
        }
    }

    private fun toPostfix() {
        position = 0
        val operatorsStack = Stack<Char>()
        while (position < cache.length){
            var symbol: Char = cache[position]
            if(symbol.isDigit()) {
                postfixExpr += readNumber(cache) + " "
            } else if(symbol == '(') {
                operatorsStack.push(symbol)
            } else if(symbol == ')') {
                while(operatorsStack.isNotEmpty() && operatorsStack.peek() != '(') {
                    postfixExpr += operatorsStack.pop()
                }
                operatorsStack.pop()
            } else if(priorityMap.containsKey(symbol)) {
                if (symbol == '-' && (position == 0 || (position > 1 && priorityMap.containsKey(cache[position-1]))))
                    symbol = '~';
                while(operatorsStack.isNotEmpty() && priorityMap[operatorsStack.peek()]!! >= priorityMap[symbol]!!) {
                    postfixExpr += operatorsStack.pop()
                }
                operatorsStack.push(symbol)
            }
            position++
        }
        for(c in operatorsStack) {
            postfixExpr += c
        }
    }

    private fun evaluateExpression() : String {
        if(cache[0] == '-') {
            cache = "0$cache"
        }
        cache = "($cache)"
        val localsStack = Stack<Double>()
        toPostfix()
        position = 0
        while(position < postfixExpr.length) {
            val symbol: Char = postfixExpr[position]

            if(symbol.isDigit()) {
                val number: String = readNumber(postfixExpr)
                localsStack.push(number.toDouble())
            } else if(priorityMap.containsKey(symbol)) {
                if (symbol == '~') {
                    val last: Double = if (localsStack.isNotEmpty()) localsStack.pop() else 0.0
                    localsStack.push(execute('-', 0.0, last))
                    continue;
                }
                var second: Double = if (localsStack.isNotEmpty()) localsStack.pop() else 0.0
                if (symbol == '÷' && second == 0.0) {
                    cache = "Error"
                    postfixExpr = ""
                    return ""
                }
                var first: Double = if (localsStack.isNotEmpty()) localsStack.pop() else 0.0
                localsStack.push(execute(symbol, first, second))
            }
            position++
        }
        postfixExpr = ""
        cache = "" + localsStack.pop()
        return cache
    }

}