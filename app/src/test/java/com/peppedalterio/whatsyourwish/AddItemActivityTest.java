package com.peppedalterio.whatsyourwish;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AddItemActivityTest {

    private AddItemActivity aia;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    public void newObject() throws Exception {
        aia = new AddItemActivity();
    }

    @Test
    public void testValidate1(){

        boolean result = false;

        result = aia.validate("",
                "");
        assertEquals(result, false);

    }

    @Test
    public void testValidate2() {

        boolean result = false;

        result = aia.validate("ABC",
                "");
        assertEquals(result, false);
    }

    @Test
    public void testValidate3() {

        boolean result = false;

        result = aia.validate("",
                "ABC");
        assertEquals(result, false);

    }

    @Test
    public void testValidate4() {

        boolean result = false;

        result = aia.validate("12345678901234567890",
                "abc");
        assertEquals(result, true);


    }

    @Test
    public void testValidate5() {

        boolean result = false;

        result = aia.validate("123456789012345678901",
                "abc");
        assertEquals(result, false);

    }

    @Test
    public void testValidate6() {

        boolean result = false;

        result = aia.validate("12345678901234567890",
                "12345678901234567890123456789012345678901234567890");
        assertEquals(result, true);

    }

    @Test
    public void testValidate7() {

        boolean result = false;

        result = aia.validate("12345678901234567890",
                "123456789012345678901234567890123456789012345678901");
        assertEquals(result, false);

    }

    @Test
    public void testValidate9() {

        boolean result = false;

        result = aia.validate("Titolo di \r\n prova",
                "Descrizione\r\n di prova");

        assertEquals(result, false);

    }

    @Test
    public void testValidate10() {

        boolean result = false;

        result = aia.validate("Titolo di \r\n prova",
                "Descrizione di prova");

        assertEquals(result, false);

    }

    @Test
    public void testValidate11() {

        boolean result = false;

        result = aia.validate("Titolo di prova",
                "Descrizione\r\n di prova");

        assertEquals(result, false);

    }

    @Test
    public void testValidate12() {

        boolean result = false;

        result = aia.validate("",
                "Descrizione\r\n di prova");

        assertEquals(result, false);

    }

    @Test
    public void testValidate13() {

        boolean result = false;

        result = aia.validate("Computer Nobetook",
                "Buona CPU");

        assertEquals(result, true);

    }

    @Test
    public void testValidate14() {

        boolean result = false;

        result = aia.validate("Titolo di \r prova",
                "Descrizione\n di prova");

        assertEquals(result, true);

    }

}
