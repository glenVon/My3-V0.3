package com.egon.my3

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.egon.my3.data.network.RetrofitClient
import com.egon.my3.network.NetworkConfig
import com.egon.my3.presentation.screens.DevSettingsContent
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DevSettingsComposeTest {
    
    // Usamos createComposeRule en lugar de createAndroidComposeRule<MainActivity>
    // para probar el Composable en aislamiento sin lanzar toda la app.
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        // Asegurar un estado limpio antes del test
        NetworkConfig.clearOverride()
    }

    @After
    fun tearDown() {
        // Limpiar después del test
        NetworkConfig.clearOverride()
    }

    @Test
    fun saveOverrideRecreatesRetrofit() {
        val testUrl = "http://10.123.45.6:8080/"
        
        composeTestRule.setContent {
            DevSettingsContent(onClose = {})
        }

        // Buscar el campo de texto por su etiqueta
        val urlNode = composeTestRule.onNodeWithText("Base URL")
        
        // Reemplazar el texto por defecto
        urlNode.performTextClearance()
        urlNode.performTextInput(testUrl)

        // Clic en guardar
        val saveButton = composeTestRule.onNodeWithText("Guardar y Reiniciar")
        saveButton.performClick()

        // Verificar que RetrofitClient se haya actualizado
        val current = RetrofitClient.getCurrentBaseUrlForTesting()
        
        // La lógica de normalización asegura que termine en /
        val expected = if (testUrl.endsWith("/")) testUrl else "$testUrl/"
        
        assertEquals(expected, current)
    }
}
