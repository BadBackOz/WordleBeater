package com.ohz.util;

import com.ohz.elements.CustomDropdownWebElement;
import com.ohz.elements.CustomInputWebElement;
import com.ohz.elements.CustomWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.lang.reflect.Field;

public class CustomElementFieldDecorator implements FieldDecorator {


    @Override
    public Object decorate(ClassLoader loader, Field field) {
        FindBy findBy = field.getAnnotation(FindBy.class);
        if (CustomInputWebElement.class.isAssignableFrom(field.getType())) {
            return new CustomInputWebElement(findBy.xpath());
        } else if (CustomWebElement.class.isAssignableFrom(field.getType())) {
            return new CustomWebElement(findBy.xpath());
        } else if (CustomDropdownWebElement.class.isAssignableFrom(field.getType())) {
            return new CustomDropdownWebElement(findBy.xpath());
        }

        return null;
    }
}
