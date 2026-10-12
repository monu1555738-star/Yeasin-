package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BSL CALLING", appName)
  }

  @Test
  fun `verify country validation rules`() {
    val bd = com.example.model.CountryData.countries.first { it.code == "BD" }
    assertTrue(bd.isValid("1712345678"))
    assertFalse(bd.isValid("123"))

    val us = com.example.model.CountryData.countries.first { it.code == "US" }
    assertTrue(us.isValid("2025550143"))
  }
}
