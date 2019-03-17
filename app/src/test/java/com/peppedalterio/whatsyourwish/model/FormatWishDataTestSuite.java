package com.peppedalterio.whatsyourwish.model;

import org.junit.jupiter.api.*;
import com.peppedalterio.whatsyourwish.util.WishStrings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class FormatWishDataTestSuite {

    static private UserWishlistModel wishlistModel;
    static final private String local_self_assigned = "Self-assigned";
    static final private String local_assign_date = "Since (dd-mm-yyyy)";
    static final private String local_description = "Description";
    static final private String SEPARATOR = WishStrings.SEPARATOR_TOKEN;

    @BeforeAll
    static void setUp() {
        wishlistModel = new UserWishlistModel(local_self_assigned, local_assign_date, local_description);
    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is nullstring
     processingDate is notvaliddate
     */
    @Test
    void test_formatWishDataStr_0() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", null, "35-12-2020");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is validnumber
     processingDate is nullstring
     */
    @Test
    void test_formatWishDataStr_1() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "+391234567890", null);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890" +
                SEPARATOR + local_self_assigned + ": " + "+391234567890" +
                SEPARATOR + local_assign_date + ": " + today);

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is validnumber
     processingDate is validdate
     */
    @Test
    void test_formatWishDataStr_2() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "+391234567890", "14-03-2019");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890" +
                SEPARATOR + local_self_assigned + ": " + "+391234567890" +
                SEPARATOR + local_assign_date + ": " + "14-03-2019");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is emptystring
     processingDate is emptystring
     */
    @Test
    void test_formatWishDataStr_3() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "", "");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is emptystring
     processingDate is validdate
     */
    @Test
    void test_formatWishDataStr_4() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "", "14-03-2019");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is validnumber
     processingDate is notvaliddate
     */
    @Test
    void test_formatWishDataStr_5() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "+391234567890", "35-12-2020");

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890" +
                SEPARATOR + local_self_assigned + ": " + "+391234567890" +
                SEPARATOR + local_assign_date + ": " + today);

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is emptystring
     processingDate is notvaliddatepattern
     */
    @Test
    void test_formatWishDataStr_6() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "", "14/03/2019");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is nullstring
     processingDate is emptystring
     */
    @Test
    void test_formatWishDataStr_7() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", null, "");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is nullstring
     processingDate is validdate
     */
    @Test
    void test_formatWishDataStr_8() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", null, "14-03-2019");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is validnumber
     processingDate is notvaliddatepattern
     */
    @Test
    void test_formatWishDataStr_9() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "+391234567890", "14/03/2019");

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890" +
                SEPARATOR + local_self_assigned + ": " + "+391234567890" +
                SEPARATOR + local_assign_date + ": " + today);

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is nullstring
     processingDate is notvaliddatepattern
     */
    @Test
    void test_formatWishDataStr_10() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", null, "14/03/2019");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is nullstring
     processingDate is nullstring
     */
    @Test
    void test_formatWishDataStr_11() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", null, null);

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is validnumber
     processingDate is emptystring
     */
    @Test
    void test_formatWishDataStr_12() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "+391234567890", "");

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890" +
                SEPARATOR + local_self_assigned + ": " + "+391234567890" +
                SEPARATOR + local_assign_date + ": " + today);

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is emptystring
     processingDate is nullstring
     */
    @Test
    void test_formatWishDataStr_13() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "", null);

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     title is 40charstring
     description is 50charstring
     assignee is emptystring
     processingDate is notvaliddate
     */
    @Test
    void test_formatWishDataStr_14() {

        String result = wishlistModel.formatWishDataStr("1234567890123456789012345678901234567890", "12345678901234567890123456789012345678901234567890", "", "35-12-2020");

        assertEquals(result, "1234567890123456789012345678901234567890" + SEPARATOR + local_description + ": " + "12345678901234567890123456789012345678901234567890");

    }

    /**
     * Empty title -> IllegalArgumentException
     */
    @Test
    void test_formatWishDataStr15() {

        assertThrows(IllegalArgumentException.class, () ->
                wishlistModel.formatWishDataStr("", "Description", "+391234567890", "14-03-2019")
        );

    }

    /**
     * null title -> IllegalArgumentException
     */
    @Test
    void test_formatWishDataStr16() {

        assertThrows(IllegalArgumentException.class, () ->
                wishlistModel.formatWishDataStr(null, "Description", "+391234567890", "14-03-2019")
        );

    }

    /**
     * invalid title -> IllegalArgumentException
     */
    @Test
    void test_formatWishDataStr17() {

        assertThrows(IllegalArgumentException.class, () ->
                wishlistModel.formatWishDataStr("prova\r\n", "Description", "+391234567890", "14-03-2019")
        );

    }

    /**
     * invalid description -> IllegalArgumentException
     */
    @Test
    void test_formatWishDataStr18() {

        assertThrows(IllegalArgumentException.class, () ->
                wishlistModel.formatWishDataStr("titolo", "Desc\r\nri", "+391234567890", "14-03-2019")
        );

    }

}