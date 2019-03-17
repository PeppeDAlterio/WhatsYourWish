package com.peppedalterio.whatsyourwish.model;

import com.peppedalterio.whatsyourwish.util.WishStrings;

import org.junit.jupiter.api.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class FormatWishDataTestOldButGood {

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
     * Correct use of appendAssignee
     */
    @Test
    void test_appendAssignee1(){

        String str = wishlistModel.appendAssignee("+391234567890", "14-03-2019");

        assertEquals(str, SEPARATOR +
                local_self_assigned + ": +391234567890" +
                SEPARATOR +
                local_assign_date + ": 14-03-2019");

    }

    /**
     * empty asignee -> should return empty string
     */
    @Test
    void test_appendAssignee2(){

        String str = wishlistModel.appendAssignee("", "14-03-2019");

        assertEquals("", str);

    }

    /**
     * empty processingDate -> should return empty string
     */
    @Test
    void test_appendAssignee3(){

        String str = wishlistModel.appendAssignee("+391234567890", "");

        assertEquals("", str);

    }

    /**
     * wrong processingDate (dd-MM-yyyy) -> should return empty string
     */
    @Test
    void test_appendAssignee4(){

        String str = wishlistModel.appendAssignee("+391234567890", "14/03/2019");

        assertEquals("", str);

    }

    /**
     * null processingDate -> should return empty string
     */
    @Test
    void test_appendAssignee5(){

        String str = wishlistModel.appendAssignee("+391234567890", null);

        assertEquals("", str);

    }

    /**
     * null assignee -> should return empty string
     */
    @Test
    void test_appendAssignee6(){

        String str = wishlistModel.appendAssignee(null, "14-03-2019");

        assertEquals("", str);

    }

    /**
     * Correct use of the formatter
     */
    @Test
    void test_formatWishDataStr1() {
        String str = wishlistModel.formatWishDataStr("Titolo", "Descrizione", "+391234567890", "14-03-2019");
        System.out.println(str);

        assertEquals(str, "Titolo" + SEPARATOR + local_description + ": Descrizione" +
                SEPARATOR +
                local_self_assigned + ": +391234567890" +
                SEPARATOR +
                local_assign_date + ": 14-03-2019");

    }

    /**
     * Format with null assignee -> should NOT display assignment's info
     */
    @Test
    void test_formatWishDataStr2() {
        //String formatWishDataStr(String title, String description, String assignee, String processingDate)

        String str = wishlistModel.formatWishDataStr("Titolo", "Descrizione", null, "14/03/2019");
        System.out.println(str);

        assertEquals(str, "Titolo" + SEPARATOR + local_description + ": Descrizione");

    }

    /**
     * Format wish null date -> should use today
     */
    @Test
    void test_formatWishDataStr3() {

        String str = wishlistModel.formatWishDataStr("Titolo", "Descrizione", "+391234567890", null);
        System.out.println(str);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        assertEquals(str, "Titolo" + SEPARATOR + local_description + ": Descrizione" +
                SEPARATOR +
                local_self_assigned + ": +391234567890" +
                SEPARATOR +
                local_assign_date + ": " + today);

    }

    /**
     * Format with wrong data pattern -> should use today's date
     */
    @Test //wrong date format should be converted into today's date
    void test_formatWishDataStr4() {
        //String formatWishDataStr(String title, String description, String assignee, String processingDate)

        String str = wishlistModel.formatWishDataStr("Titolo", "Descrizione", "+391234567890", "14/03/2019");
        System.out.println(str);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        assertEquals(str, "Titolo" + SEPARATOR + local_description + ": Descrizione" +
                SEPARATOR +
                local_self_assigned + ": +391234567890" +
                SEPARATOR +
                local_assign_date + ": " + today);

    }

    /**
     * Format with empty processing date -> should use today's date
     */
    @Test
    void test_formatWishDataStr5() {

        String str = wishlistModel.formatWishDataStr("Titolo", "", "+391234567890", "");
        System.out.println(str);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        assertEquals(str, "Titolo" +
                SEPARATOR +
                local_self_assigned + ": +391234567890" +
                SEPARATOR +
                local_assign_date + ": " + today);

    }

    /**
     * Format with empty descrption -> everything's displayed but description
     */
    @Test
    void test_formatWishDataStr6() {

        String str = wishlistModel.formatWishDataStr("Titolo", "", "+391234567890", "14-03-2019");
        System.out.println(str);

        assertEquals(str, "Titolo" +
                SEPARATOR +
                local_self_assigned + ": +391234567890" +
                SEPARATOR +
                local_assign_date + ": 14-03-2019" );

    }

    /**
     * Empty title -> RuntimeException
     */
    @Test
    void test_formatWishDataStr7() {

        assertThrows(IllegalArgumentException.class, () ->
            wishlistModel.formatWishDataStr("", "Description", "+391234567890", "14-03-2019")
        );

    }

    /**
     * null title -> RuntimeException
     */
    @Test
    void test_formatWishDataStr8() {

        assertThrows(NullPointerException.class, () ->
            wishlistModel.formatWishDataStr(null, "Description", "+391234567890", "14-03-2019")
        );

    }


    /**
     * Correct pattern dd-MM-yyyy
     */
    @Test
    void test_isValidDate1() {
        boolean result = UserWishlistModel.isValidDate("14-03-2019");
        assertTrue(result);
    }

    @Test
    void test_isValidDate2() {
        boolean result = UserWishlistModel.isValidDate("14/03/2019");
        assertFalse(result);
    }

    @Test
    void test_isValidDate3() {
        boolean result = UserWishlistModel.isValidDate("");
        assertFalse(result);
    }

    @Test
    void test_isValidDate4() {
        boolean result = UserWishlistModel.isValidDate(null);
        assertFalse(result);
    }

    /**
     * Correct pattern but wrong date
     */
    @Test
    void test_isValidDate5() {
        boolean result = wishlistModel.isValidDate("31-02-2019");
        assertFalse(result);
    }

}
