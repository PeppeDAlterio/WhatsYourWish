package com.peppedalterio.whatsyourwish.activity;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AddItemValidateTestSuite {

    private AddItemActivity aia;

    @BeforeEach
    void newObject() {
        aia = new AddItemActivity();
    }

    /**
     title is nullstring
     description is emptystring
     */
    @Test
    void test_validate_0() {

        assertThrows(NullPointerException.class, () ->
                aia.validate(null, "")
        );


    }

    /**
     title is notallowed
     description is morethan50
     */
    @Test
    void test_validate_1() {

        boolean result = aia.validate("test\r\ntest", "12345678901234567890123456789012345678901234567890_");

        assertEquals(false, result);


    }

    /**
     title is notallowed
     description is nullstring
     */
    @Test
    void test_validate_2() {

        boolean result = aia.validate("test\r\ntest", null);

        assertEquals(false, result);


    }

    /**
     title is emptystring
     description is morethan50
     */
    @Test
    void test_validate_3() {

        boolean result = aia.validate("", "12345678901234567890123456789012345678901234567890_");

        assertEquals(false, result);


    }

    /**
     title is nullstring
     description is morethan50
     */
    @Test
    void test_validate_4() {

        assertThrows(NullPointerException.class, () ->
                aia.validate(null, "")
        );


    }

    /**
     title is nullstring
     description is 50charstring
     */
    @Test
    void test_validate_5() {

        assertThrows(NullPointerException.class, () ->
                aia.validate(null, "")
        );


    }

    /**
     title is morethan40
     description is 50charstring
     */
    @Test
    void test_validate_6() {

        boolean result = aia.validate("1234567890123456789012345678901234567890_", "12345678901234567890123456789012345678901234567890");

        assertEquals(false, result);


    }

    /**
     title is notallowed
     description is notallowed
     */
    @Test
    void test_validate_7() {

        boolean result = aia.validate("test\r\ntest", "test\r\ntest");

        assertEquals(false, result);


    }

    /**
     title is nullstring
     description is nullstring
     */
    @Test
    void test_validate_8() {

        assertThrows(NullPointerException.class, () ->
                aia.validate(null, "")
        );


    }

    /**
     title is emptystring
     description is emptystring
     */
    @Test
    void test_validate_9() {

        boolean result = aia.validate("", "");

        assertEquals(false, result);


    }

    /**
     title is morethan40
     description is emptystring
     */
    @Test
    void test_validate_10() {

        boolean result = aia.validate("1234567890123456789012345678901234567890_", "");

        assertEquals(false, result);


    }

    /**
     title is emptystring
     description is nullstring
     */
    @Test
    void test_validate_11() {

        boolean result = aia.validate("", null);

        assertEquals(false, result);


    }

    /**
     title is emptystring
     description is 50charstring
     */
    @Test
    void test_validate_12() {

        boolean result = aia.validate("", "12345678901234567890123456789012345678901234567890");

        assertEquals(false, result);


    }

    /**
     title is notallowed
     description is 50charstring
     */
    @Test
    void test_validate_13() {

        boolean result = aia.validate("test\r\ntest", "12345678901234567890123456789012345678901234567890");

        assertEquals(false, result);


    }

    /**
     title is 40charstring
     description is emptystring
     */
    @Test
    void test_validate_14() {

        boolean result = aia.validate("1234567890123456789012345678901234567890", "");

        assertEquals(true, result);


    }

    /**
     title is 40charstring
     description is morethan50
     */
    @Test
    void test_validate_15() {

        boolean result = aia.validate("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890_");

        assertEquals(false, result);


    }

    /**
     title is emptystring
     description is notallowed
     */
    @Test
    void test_validate_16() {

        boolean result = aia.validate("", "test\r\ntest");

        assertEquals(false, result);


    }

    /**
     title is 40charstring
     description is 50charstring
     */
    @Test
    void test_validate_17() {

        boolean result = aia.validate("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890");

        assertEquals(true, result);


    }

    /**
     title is nullstring
     description is notallowed
     */
    @Test
    void test_validate_18() {

        assertThrows(NullPointerException.class, () ->
                aia.validate(null, "")
        );


    }

    /**
     title is 40charstring
     description is notallowed
     */
    @Test
    void test_validate_19() {

        boolean result = aia.validate("1234567890123456789012345678901234567890", "test\r\ntest");

        assertEquals(false, result);


    }

    /**
     title is morethan40
     description is notallowed
     */
    @Test
    void test_validate_20() {

        boolean result = aia.validate("1234567890123456789012345678901234567890_", "test\r\ntest");

        assertEquals(false, result);


    }

    /**
     title is morethan40
     description is nullstring
     */
    @Test
    void test_validate_21() {

        boolean result = aia.validate("1234567890123456789012345678901234567890_", null);

        assertEquals(false, result);


    }

    /**
     title is morethan40
     description is morethan50
     */
    @Test
    void test_validate_22() {

        boolean result = aia.validate("1234567890123456789012345678901234567890_", "12345678901234567890123456789012345678901234567890_");

        assertEquals(false, result);


    }

    /**
     title is 40charstring
     description is nullstring
     */
    @Test
    void test_validate_23() {

        boolean result = aia.validate("1234567890123456789012345678901234567890", null);

        assertEquals(true, result);


    }

    /**
     title is notallowed
     description is emptystring
     */
    @Test
    void test_validate_24() {

        boolean result = aia.validate("test\r\ntest", "");

        assertEquals(false, result);


    }

}
