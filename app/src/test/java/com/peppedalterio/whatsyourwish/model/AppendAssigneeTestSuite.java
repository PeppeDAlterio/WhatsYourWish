package com.peppedalterio.whatsyourwish.model;

import org.junit.jupiter.api.*;
import com.peppedalterio.whatsyourwish.util.WishStrings;

import static org.junit.jupiter.api.Assertions.*;

class AppendAssigneeTestSuite {

    static final private String local_self_assigned = "Self-assigned";
    static final private String local_assign_date = "Since (dd-mm-yyyy)";
    static final private String local_description = "Description";
    static final private String SEPARATOR = WishStrings.SEPARATOR_TOKEN;
    static private UserWishlistModel wishlistModel;

    @BeforeAll
    static void setUp() {
        wishlistModel = new UserWishlistModel(local_self_assigned, local_assign_date, local_description);
    }

    /**
     assignee is emptystring
     processingDate is notvaliddate
     */
    @Test
    void test_appendAssignee_0() {

        String result = wishlistModel.appendAssignee("", "35-12-2020");

        assertEquals("", result);


    }
    /**
     assignee is nullstring
     processingDate is notvaliddate
     */
    @Test
    void test_appendAssignee_1() {

        String result = wishlistModel.appendAssignee(null, "35-12-2020");

        assertEquals("", result);


    }
    /**
     assignee is emptystring
     processingDate is nullstring
     */
    @Test
    void test_appendAssignee_2() {

        String result = wishlistModel.appendAssignee("", null);

        assertEquals("", result);


    }
    /**
     assignee is validnumber
     processingDate is nullstring
     */
    @Test
    void test_appendAssignee_3() {

        String result = wishlistModel.appendAssignee("+391234567890", null);

        assertEquals("", result);


    }
    /**
     assignee is validnumber
     processingDate is emptystring
     */
    @Test
    void test_appendAssignee_4() {

        String result = wishlistModel.appendAssignee("+391234567890", "");

        assertEquals("", result);


    }
    /**
     assignee is validnumber
     processingDate is notvaliddate
     */
    @Test
    void test_appendAssignee_5() {

        String result = wishlistModel.appendAssignee("+391234567890", "35-12-2020");

        assertEquals("", result);


    }
    /**
     assignee is nullstring
     processingDate is validdate
     */
    @Test
    void test_appendAssignee_6() {

        String result = wishlistModel.appendAssignee(null, "14-03-2019");

        assertEquals("", result);


    }
    /**
     assignee is emptystring
     processingDate is emptystring
     */
    @Test
    void test_appendAssignee_7() {

        String result = wishlistModel.appendAssignee("", "");

        assertEquals("", result);


    }
    /**
     assignee is nullstring
     processingDate is notvaliddatepattern
     */
    @Test
    void test_appendAssignee_8() {

        String result = wishlistModel.appendAssignee(null, "14/03/2019");

        assertEquals("", result);


    }
    /**
     assignee is validnumber
     processingDate is validdate
     */
    @Test
    void test_appendAssignee_9() {

        String result = wishlistModel.appendAssignee("+391234567890", "14-03-2019");

        assertEquals(SEPARATOR + local_self_assigned + ": +391234567890" + SEPARATOR + local_assign_date + ": 14-03-2019", result);


    }
    /**
     assignee is nullstring
     processingDate is emptystring
     */
    @Test
    void test_appendAssignee_10() {

        String result = wishlistModel.appendAssignee(null, "");

        assertEquals("", result);


    }
    /**
     assignee is validnumber
     processingDate is notvaliddatepattern
     */
    @Test
    void test_appendAssignee_11() {

        String result = wishlistModel.appendAssignee("+391234567890", "14/03/2019");

        assertEquals("", result);


    }
    /**
     assignee is emptystring
     processingDate is notvaliddatepattern
     */
    @Test
    void test_appendAssignee_12() {

        String result = wishlistModel.appendAssignee("", "14/03/2019");

        assertEquals("", result);


    }
    /**
     assignee is emptystring
     processingDate is validdate
     */
    @Test
    void test_appendAssignee_13() {

        String result = wishlistModel.appendAssignee("", "14-03-2019");

        assertEquals("", result);


    }
    /**
     assignee is nullstring
     processingDate is nullstring
     */
    @Test
    void test_appendAssignee_14() {

        String result = wishlistModel.appendAssignee(null, null);

        assertEquals("", result);


    }


}
