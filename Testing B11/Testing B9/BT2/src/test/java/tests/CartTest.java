package tests;

import framework.base.BaseTest;
import framework.pages.CartPage;
import framework.pages.CheckoutPage;
import framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CartTest extends BaseTest {

    @Test
    public void testFluentFlowLoginAddFirstAndGoToCart() {
        CartPage cartPage = new LoginPage(getDriver())
                .login("standard_user", "secret_sauce")
                .addFirstItemToCart()
                .goToCart();

        Assert.assertTrue(cartPage.getItemCount() >= 1, "Cart should contain at least one item");
    }

    @Test
    public void testRemoveFirstItemFromCart() {
        CartPage cartPage = new LoginPage(getDriver())
                .login("standard_user", "secret_sauce")
                .addFirstItemToCart()
                .goToCart()
                .removeFirstItem();

        Assert.assertEquals(cartPage.getItemCount(), 0, "Cart should be empty after removing the only item");
    }

    @Test
    public void testGoToCheckoutAndItemNamesAvailable() {
        CartPage cartPage = new LoginPage(getDriver())
                .login("standard_user", "secret_sauce")
                .addItemByName("Sauce Labs Backpack")
                .goToCart();

        List<String> names = cartPage.getItemNames();
        Assert.assertTrue(names.contains("Sauce Labs Backpack"), "Cart should contain selected item by name");

        CheckoutPage checkoutPage = cartPage.goToCheckout();
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should be loaded");
    }
}
