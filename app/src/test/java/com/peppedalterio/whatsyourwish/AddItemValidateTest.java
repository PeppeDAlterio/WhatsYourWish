package com.peppedalterio.whatsyourwish;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AddItemValidateTest {

    private AddItemActivity aia;


    @BeforeEach
    void newObject() {
        aia = new AddItemActivity();
    }

    @Test
    void testValidate1(){

        boolean result;

        result = aia.validate("",
                "");

        assertFalse(result);

    }

    @Test
    void testValidate2() {

        boolean result;

        result = aia.validate("ABC",
                "");

        assertTrue(result);
    }

    @Test
    void testValidate3() {

        boolean result;

        result = aia.validate("",
                "ABC");

        assertFalse(result);

    }

    @Test
    void testValidate4() {

        boolean result;

        result = aia.validate("12345678901234567890",
                "abc");

        assertTrue(result);


    }

    @Test
    void testValidate5() {

        boolean result;

        result = aia.validate("123456789012345678901",
                "abc");

        assertFalse(result);

    }

    @Test
    void testValidate6() {

        boolean result;

        result = aia.validate("12345678901234567890",
                "12345678901234567890123456789012345678901234567890");

        assertTrue(result);

    }

    @Test
    void testValidate7() {

        boolean result;

        result = aia.validate("12345678901234567890",
                "123456789012345678901234567890123456789012345678901");

        assertFalse(result);

    }

    @Test
    void testValidate9() {

        boolean result;

        result = aia.validate("Titolo di \r\n prova",
                "Descrizione\r\n di prova");

        assertFalse(result);

    }

    @Test
    void testValidate10() {

        boolean result;

        result = aia.validate("Titolo di \r\n prova",
                "Descrizione di prova");

        assertFalse(result);

    }

    @Test
    void testValidate11() {

        boolean result;

        result = aia.validate("Titolo di prova",
                "Descrizione\r\n di prova");

        assertFalse(result);

    }

    @Test
    void testValidate12() {

        boolean result;

        result = aia.validate("",
                "Descrizione\r\n di prova");

        assertFalse(result);

    }

    @Test
    void testValidate13() {

        boolean result;

        result = aia.validate("Computer Nobetook",
                "Buona CPU");

        assertTrue(result);

    }

    @Test
    void testValidate14() {

        boolean result;

        result = aia.validate("Titolo di \r prova",
                "Descrizione\n di prova");

        assertTrue(result);

    }

    @Test
    void testValidate15() {

        boolean result;

        result = aia.validate("   ",
                "Descrizione di prova");

        assertFalse(result);

    }

}
