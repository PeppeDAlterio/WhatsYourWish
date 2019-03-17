package com.peppedalterio.whatsyourwish.model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class IsValidDateTestSuite {

    /**
     inDate is notvaliddatepattern
     */
    @Test
    void test_isValidDate_0() {

        boolean result = UserWishlistModel.isValidDate("14/03/2019");

        assertEquals(result, false);

    }

    /**
     inDate is emptystring
     */
    @Test
    void test_isValidDate_1() {

        boolean result = UserWishlistModel.isValidDate("");

        assertEquals(result, false);

    }

    /**
     inDate is nullstring
     */
    @Test
    void test_isValidDate_2() {

        boolean result = UserWishlistModel.isValidDate(null);

        assertEquals(result, false);

    }

    /**
     inDate is validdate
     */
    @Test
    void test_isValidDate_3() {

        boolean result = UserWishlistModel.isValidDate("14-03-2019");

        assertEquals(result, true);

    }

    /**
     inDate is 29febisbissextile
     */
    @Test
    void test_isValidDate_4() {

        boolean result = UserWishlistModel.isValidDate("29-02-2016");

        assertEquals(result, true);

    }

    /**
     inDate is 29febnotbissextile
     */
    @Test
    void test_isValidDate_5() {

        boolean result = UserWishlistModel.isValidDate("29-02-2019");

        assertEquals(result, false);

    }

    /**
     inDate is notvaliddate
     */
    @Test
    void test_isValidDate_6() {

        boolean result = UserWishlistModel.isValidDate("35-12-2020");

        assertEquals(result, false);

    }

}
