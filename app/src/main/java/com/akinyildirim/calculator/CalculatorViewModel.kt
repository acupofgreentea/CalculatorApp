package com.akinyildirim.calculator

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }

    fun onAction(action: CalculatorAction) {
        when(action){
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> calculate()
        }
    }

    private fun delete(){
        when{
            state.number2.isNotBlank() -> state = state.copy(number2 = state.number2.dropLast(1))
            state.operation != null -> state = state.copy(operation = null)
            state.number1.isNotBlank() -> state = state.copy(number1 = state.number1.dropLast(1))
        }
    }

    private fun calculate() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        val operation = state.operation

        if(number1 == null || number2 == null || operation == null)
            return

        val result = when(operation){
            is CalculatorOperation.Add -> number1 + number2
            is CalculatorOperation.Subtract -> number1 - state.number2.toDouble()
            is CalculatorOperation.Multiply -> number1 * number2
            is CalculatorOperation.Divide -> number1 / number2
        }

        val resultString : String
        val resultInt = result.toInt()
        val resultDouble = result - resultInt

        if(resultDouble == 0.0)
            resultString = resultInt.toString()
        else
            resultString = result.toString()

        state = state.copy(number1 = resultString.take(MAX_NUM_LENGTH), number2 = "", operation = null)
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if(state.number1.isBlank())
            return

        state = state.copy(operation = operation)
    }

    private fun enterDecimal() {
        if(state.operation == null && !state.number1.contains(".") && state.number1.isNotBlank())
        {
            state = state.copy(number1 = state.number1 + ".")
            return
        }else if (!state.number2.contains(".") && state.number2.isNotBlank()){
            state = state.copy(number2 = state.number2 + ".")
        }
    }

    private fun enterNumber(number: Int) {
        Log.e("CalculatorViewModel", "enterNumber: $number")
        if(state.operation == null) {
            if(state.number1.length >= MAX_NUM_LENGTH) {
                return
            }
            state = state.copy(
                number1 = state.number1 + number
            )
            return
        }
        if(state.number2.length >= MAX_NUM_LENGTH) {
            return
        }
        state = state.copy(
            number2 = state.number2 + number
        )
    }
}