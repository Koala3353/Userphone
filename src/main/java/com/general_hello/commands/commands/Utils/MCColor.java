package com.general_hello.commands.commands.Utils;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class MCColor
{
    public static String translate(char character, String text)
    {
        char[] charArray = text.toCharArray();
        AttributedStringBuilder attributedStringBuilder = new AttributedStringBuilder();
        for (int i = 0; i < charArray.length; i++)
        {
            if (charArray[i] == character && charArray.length > i + 1)
            {
                char colorcode = charArray[i + 1];
                boolean validCode = true;
                switch (colorcode)
                {
                    case '4':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xAA, 0x00, 0x00));
                        break;
                    case 'c':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xFF, 0x55, 0x55));
                        break;
                    case '6':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xFF, 0xAA, 0x00));
                        break;
                    case 'e':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xFF, 0xFF, 0x55));
                        break;
                    case '2':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x00, 0xAA, 0x00));
                        break;
                    case 'a':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x55, 0xFF, 0x55));
                        break;
                    case 'b':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x55, 0xFF, 0xFF));
                        break;
                    case '3':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x00, 0xAA, 0xAA));
                        break;
                    case '1':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x00, 0x00, 0xAA));
                        break;
                    case '9':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x55, 0x55, 0xFF));
                        break;
                    case 'd':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xFF, 0x55, 0xFF));
                        break;
                    case '5':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xAA, 0x00, 0xAA));
                        break;
                    case 'f':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xFF, 0xFF, 0xFF));
                        break;
                    case '7':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0xAA, 0xAA, 0xAA));
                        break;
                    case '8':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x55, 0x55, 0x55));
                        break;
                    case '0':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.foreground(0x00, 0x00, 0x00));
                        break;
                    case 'l':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.boldDefault());
                        break;
                    case 'n':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.underline());
                        break;
                    case 'o':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT.italicDefault());
                        break;
                    case 'r':
                        attributedStringBuilder.style(AttributedStyle.DEFAULT);
                        break;
                    default:
                        validCode = false;

                }
                if (validCode && i + 1 < charArray.length)
                {
                    charArray[i] = Character.MIN_VALUE;
                    charArray[i + 1] = Character.MIN_VALUE;
                }
            }
            attributedStringBuilder.append(charArray[i]);
        }
        return attributedStringBuilder.toAnsi();
    }

    public static String translate(String text)
    {
        return translate('&', text);
    }
}