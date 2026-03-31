package tests;

import framework.base.BaseTest;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void testLoginSuccess() {
        LoginPage loginPage = new LoginPage(getDriver());
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isLoaded(), "Inventory page should be loaded after login success");
    }

    @Test
    public void testLoginErrorDisplayedWithWrongPassword() {
        LoginPage loginPage = new LoginPage(getDriver());
        LoginPage currentPage = loginPage.loginExpectingFailure("standard_user", "wrongpass");
        Assert.assertTrue(currentPage.isErrorDisplayed(), "Error message should be displayed on failed login");
    }

    @Test
    public void testLoginErrorMessageContent() {
        LoginPage loginPage = new LoginPage(getDriver());
        String msg = loginPage
                .loginExpectingFailure("", "")
                .getErrorMessage();
        Assert.assertTrue(msg.toLowerCase().contains("required"), "Error message should mention required credentials");
    }
}
