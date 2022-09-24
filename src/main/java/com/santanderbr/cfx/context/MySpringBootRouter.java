package com.santanderbr.cfx.context;

import java.math.BigInteger;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints
 * to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

	@Override
	public void configure() {

		final String cxfUri = "cxf:https://www.dataaccess.com/webservicesserver/NumberConversion.wso?"
				+ "serviceClass=com.santanderbr.cfx.context.NumberConversionSoapImpl";

		restConfiguration().component("netty-http").host("localhost").port(12080).bindingMode(RestBindingMode.auto);

		// http://localhost:12080/conversion/number/32
		rest("/conversion").get("/number/{num}").to("direct:processConversion");

		from("direct:processConversion")
			// ----------------------------------------------------------
			.setProperty("number", header("num"))
			// Cast the number to Word Expression, limited up to 50
			.bean(NumberToWordBean::new)
			// -----------------------------------------------------------------
			.setProperty("propNum", body())
			//.log("${exchangeProperty.propNum}")
			.transform(exchangeProperty("number"))
			.convertBodyTo(BigInteger.class)
			//.log("Before Send --- ${body} ---")
			// ---------- CXF CONSUMER --------------------------------------------------------------------
			.setHeader(CxfConstants.OPERATION_NAME, constant("NumberToWords"))
			.setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://www.dataaccess.com/webservicesserver/"))
			.to(cxfUri)
			// -------------------------------------------------------------------------------------------------------------
			.log("After Service Invocation --- ${body} ---")
			.log("Property Saved --- ${exchangeProperty.propNum}")
			// --- Needs Conversion to apply trim 
			.convertBodyTo(String.class)
			// ------------------------------------------
			.choice()
				.when(simple("${body.trim()} =~ ${exchangeProperty.propNum}"))
					.log("equals")
				.otherwise() 
					.log("!! diff") 
			.end();

	

	}

}
