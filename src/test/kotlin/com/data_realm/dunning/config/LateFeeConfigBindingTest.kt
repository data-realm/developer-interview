package com.data_realm.dunning.config

import com.data_realm.dunning.TestFixtures
import com.data_realm.dunning.domain.PlanTier
import com.data_realm.dunning.dunning.LateFeePolicy
import com.data_realm.dunning.money.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class LateFeeConfigBindingTest(
    @param:Autowired private val properties: DunningProperties,
    @param:Autowired private val policy: LateFeePolicy,
) {

    @Test
    fun `late fee minimums in application yml keep their upper-case currency keys`() {
        val minimum = properties.lateFee.minimum
        assertEquals(setOf("GBP", "EUR", "USD", "JPY"), minimum.keys)
        assertEquals(Money.of("5.00", "GBP"), Money.of(minimum.getValue("GBP"), "GBP"))
        assertEquals(Money.of("1000", "JPY"), Money.of(minimum.getValue("JPY"), "JPY"))
        assertEquals(250, properties.lateFee.basisPoints)
    }

    @Test
    fun `the configured minimums reach the policy`() {
        val customer = TestFixtures.customer(plan = PlanTier.GROWTH)
        val gbp = TestFixtures.invoice(amount = Money.of("40.00", "GBP"))
        val jpy = TestFixtures.invoice(amount = Money.of("4800", "JPY"))
        assertEquals(Money.of("5.00", "GBP"), policy.lateFee(gbp, customer))
        assertEquals(Money.of("1000", "JPY"), policy.lateFee(jpy, customer))
    }
}
