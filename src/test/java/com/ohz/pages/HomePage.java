package com.ohz.pages;

import com.ohz.common.BaseTestClass;
import com.ohz.elements.CustomWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.ohz.util.CustomElementFieldDecorator;

public class HomePage extends BaseTestClass {

    @FindBy(xpath = "//button[@aria-label='Close']")
    public CustomWebElement howToPlayDialogCloseButton;

    @FindBy(xpath = "//button[@aria-label='backspace']")
    public CustomWebElement buttonBackspace;

    @FindBy(xpath = "//button[text()='Play']")
    public CustomWebElement buttonPlay;

    @FindBy(xpath = "//*[text()='Not in word list']")
    public CustomWebElement messageInvalidWord;


    public HomePage() {
        PageFactory.initElements(new CustomElementFieldDecorator(), this);
    }

    public void typeWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            String currCharacter = String.valueOf(word.charAt(i));
            new CustomWebElement("//button[@data-key='" + currCharacter.toLowerCase() + "']").clickWithoutLogging();
        }
        new CustomWebElement("//button[text()='enter']").clickWithoutLogging();
    }

}
