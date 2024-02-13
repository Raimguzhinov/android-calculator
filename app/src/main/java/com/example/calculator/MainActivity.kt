package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private var operand = ""
    private var eqOperator = ""
    private var cache = ""
    private var eqResult = 0.0
    private var inputOperand = false

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
                        if(!inputOperand) {
                            operand = ""
                            inputOperand = true
                        }
                        operand += button.text
                        cache += button.text
                        result.text = cache
                        findViewById<com.google.android.material.button.MaterialButton>(R.id.clear).text = "C"
                    }

                    button.text.matches(Regex("[+\\-×÷]")) -> {
                        if(!inputOperand) {
                            cache = cache.dropLast(1)
                        }
                        if(cache.isEmpty()) {
                            operand = "0"
                            cache += operand
                        }
                        eqOperator = button.text.toString()
                        cache += eqOperator

                        inputOperand = false
                        result.text = cache
                    }

                    button.text == "=" -> {
                        if(!inputOperand) {
                            cache = cache.dropLast(1)
                        }
                        formula.text = cache
                        if(cache.isNotEmpty()) {
                            var check = evaluateExpression()
                            if(check == "Error") {
                                result.text = cache
                                cache = ""
                                operand = ""
                            } else {
                                eqResult = check.toDouble()
                                if (eqResult % 1.0 == 0.0) {
                                    operand = eqResult.toInt().toString()
                                } else {
                                    operand = eqResult.toString()
                                }
                                cache = operand
                                result.text = operand
                            }
                        }
                        eqOperator = ""
                        inputOperand = true
                    }

                    button.text == "." -> {
                        if(operand.isEmpty()) {
                            operand += "0"
                        }
                        if(!operand.contains(".")) {
                            operand += "."
                            cache += "."

                        }
                        result.text = cache
                    }

                    button.text == "A/C" -> {
                        eqOperator = ""
                        operand = ""
                        formula.text = ""
                        result.text = "0"
                        cache = ""
                    }

                    button.text == "C" -> {
                        operand = ""
                        eqOperator = ""
                        formula.text = ""
                        result.text = "0"
                        button.text = "A/C"
                        cache = ""
                    }

                    button.text == "±" -> {
                        if(inputOperand) {
                            var newOperand = operand
                            if(operand.contains("-")) {
                                newOperand = if(cache == operand) operand.takeLast(operand.length - 1) else operand.takeLast(operand.length - 2).dropLast(1)
                            } else {
                                newOperand = if(cache == operand) ("-" + operand.takeLast(operand.length)) else "(-$operand)"
                            }
                            cache = cache.dropLast(operand.length)
                            operand = newOperand
                            cache += operand
                        }
                        result.text = cache
                    }

                    button.text == "%" -> {
                        if(inputOperand) {
                            val num = 0
                            var brackets = false
                            if (eqOperator.matches(Regex("[×÷]")) || operand == cache) {
                                cache = cache.dropLast(operand.length)
                                if(operand.contains("(")) {
                                    operand = operand.takeLast(operand.length - 1).dropLast(1)
                                    brackets = true
                                }
                                val num = operand.toDouble()
                                operand = (num / 100).toString()
                                cache += if (brackets) "($operand)" else operand
                                result.text = cache
                            }
                            else {
                                cache = cache.dropLast(operand.length)
                                var tempCache = cache
                                var check = evaluateExpression()
                                if(check == "Error") {
                                    result.text = cache
                                    cache = ""
                                    eqOperator = ""
                                    operand = ""
                                } else {
                                    eqResult = check.toDouble()
                                    cache = tempCache
                                    if(operand.contains("(")) {
                                        operand = operand.takeLast(operand.length - 1).dropLast(1)
                                        brackets = true
                                    }
                                    eqResult = eqResult / 100 * operand.toDouble()
                                    if (eqResult % 1.0 == 0.0) {
                                        operand = eqResult.toInt().toString()
                                    } else {
                                        operand = eqResult.toString()
                                    }
                                    cache += if (brackets) "($operand)" else operand
                                    result.text = cache
                                }
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
                    return "Error"
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
