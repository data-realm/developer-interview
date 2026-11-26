package com.data_realm.dunning.money

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoneyTest {

    @Test
    fun `parses major units using the currency's minor unit`() {
        assertEquals(1250L, Money.of("12.50", "GBP").minorUnits)
        assertEquals(1500L, Money.of("1500", "JPY").minorUnits)
    }

    @Test
    fun `percent rounds half-even to the minor unit`() {
        assertEquals(Money.of("2.50", "GBP"), Money.of("100.00", "GBP").percent(250))
        // 2.5% of 0.90 = 0.0225 -> 0.02
        assertEquals(Money.of("0.02", "GBP"), Money.of("0.90", "GBP").percent(250))
        // 2.5% of 1.00 = 0.025 -> 0.02 (half-even), not 0.03
        assertEquals(Money.of("0.02", "GBP"), Money.of("1.00", "GBP").percent(250))
    }

    @Test
    fun `refuses to mix currencies`() {
        assertFailsWith<IllegalArgumentException> { Money.of("1.00", "GBP") + Money.of("1.00", "USD") }
    }

    @Test
    fun `formats in major units`() {
        assertEquals("12.50 GBP", Money.of("12.50", "GBP").toString())
        assertEquals("4800 JPY", Money.of("4800", "JPY").toString())
    }
}
