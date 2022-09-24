package com.poc.cfx.context;

import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;

@Component
public class NumberToWordBean {

	final String[] single_digits = new String[] { "zero", "one", "two", "three", "four", "five", "six", "seven",
			"eight", "nine" };
	final String[] two_digits = new String[] { "", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
			"sixteen", "seventeen", "eighteen", "nineteen" };
	final String[] tens_multiple = new String[] { "", "", "twenty", "thirty", "forty", "fifty" };

	@Handler
	public String convertedNumber(final @Header("num") int num) {

		final int n1 = num, n2 = num;
		final int b = n1 % 10, a = n2 / 10;

		if (a == 1)
			return (two_digits[b + 1]).trim();
		else if (b == 0)
			return (tens_multiple[a]).trim();
		else
			return (tens_multiple[a] + " " + single_digits[b]).trim();

	}
}
