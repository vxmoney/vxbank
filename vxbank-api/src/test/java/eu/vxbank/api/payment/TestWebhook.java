package eu.vxbank.api.payment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class TestWebhook {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testStripeWebhook() throws Exception {

        Event event = new Event();
        event.setId("eventId01");
        event.setType("checkout.session.completed");

        Session session = new Session();
        session.setId("sessionId01");

        Gson gson = new Gson();
        String sessionJsonString = gson.toJson(session);
        JsonObject sessionJsonObject = gson.fromJson(sessionJsonString, JsonObject.class);

        Event.Data data = new Event.Data();
        data.setObject(sessionJsonObject);

        event.setData(data);

        String requestBody = gson.toJson(event);

        String webhookSigningSecret = "whsec_b36f59fd7556a24cbdd59589110a616aebb7a35167d04d2aade484c8a345af53";

        String stripeSignature = Webhook.Util.computeHmacSha256(webhookSigningSecret, getRequestBody());




        String stringResponse = mockMvc.perform(MockMvcRequestBuilders.post("/stripeWebhook")
                        .header("Stripe-Signature", getRequestSignature())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getRequestBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(stringResponse);

    }

    private String getRequestBody(){
        String body = "{\n" + "  \"id\": \"evt_3ODQD5B6aHGAQTGC0gFdv2nU\",\n" + "  \"object\": \"event\",\n" +
                "  \"api_version\": \"2023-10-16\",\n" + "  \"created\": 1700220464,\n" + "  \"data\": {\n" +
                "    \"object\": {\n" + "      \"id\": \"ch_3ODQD5B6aHGAQTGC0HCOpZHT\",\n" +
                "      \"object\": \"charge\",\n" + "      \"amount\": 1000,\n" + "      \"amount_captured\": 1000,\n" +
                "      \"amount_refunded\": 0,\n" + "      \"application\": null,\n" +
                "      \"application_fee\": null,\n" + "      \"application_fee_amount\": null,\n" +
                "      \"balance_transaction\": \"txn_3ODQD5B6aHGAQTGC0XENLj8D\",\n" +
                "      \"billing_details\": {\n" + "        \"address\": {\n" + "          \"city\": null,\n" +
                "          \"country\": \"BE\",\n" + "          \"line1\": null,\n" + "          \"line2\": null,\n" +
                "          \"postal_code\": null,\n" + "          \"state\": null\n" + "        },\n" +
                "        \"email\": \"user1@mail.com\",\n" + "        \"name\": \"user 1\",\n" +
                "        \"phone\": null\n" + "      },\n" +
                "      \"calculated_statement_descriptor\": \"WWW.VXBANK.EU\",\n" + "      \"captured\": true,\n" +
                "      \"created\": 1700220463,\n" + "      \"currency\": \"eur\",\n" + "      \"customer\": null,\n" +
                "      \"description\": null,\n" + "      \"destination\": null,\n" + "      \"dispute\": null,\n" +
                "      \"disputed\": false,\n" + "      \"failure_balance_transaction\": null,\n" +
                "      \"failure_code\": null,\n" + "      \"failure_message\": null,\n" +
                "      \"fraud_details\": {\n" + "      },\n" + "      \"invoice\": null,\n" +
                "      \"livemode\": false,\n" + "      \"metadata\": {\n" + "      },\n" +
                "      \"on_behalf_of\": null,\n" + "      \"order\": null,\n" + "      \"outcome\": {\n" +
                "        \"network_status\": \"approved_by_network\",\n" + "        \"reason\": null,\n" +
                "        \"risk_level\": \"normal\",\n" + "        \"risk_score\": 51,\n" +
                "        \"seller_message\": \"Payment complete.\",\n" + "        \"type\": \"authorized\"\n" +
                "      },\n" + "      \"paid\": true,\n" +
                "      \"payment_intent\": \"pi_3ODQD5B6aHGAQTGC0qq305H2\",\n" +
                "      \"payment_method\": \"pm_1ODQD4B6aHGAQTGC6WIPk6ro\",\n" +
                "      \"payment_method_details\": {\n" + "        \"card\": {\n" +
                "          \"amount_authorized\": 1000,\n" + "          \"brand\": \"visa\",\n" +
                "          \"checks\": {\n" + "            \"address_line1_check\": null,\n" +
                "            \"address_postal_code_check\": null,\n" + "            \"cvc_check\": \"pass\"\n" +
                "          },\n" + "          \"country\": \"US\",\n" + "          \"exp_month\": 1,\n" +
                "          \"exp_year\": 2025,\n" + "          \"extended_authorization\": {\n" +
                "            \"status\": \"disabled\"\n" + "          },\n" +
                "          \"fingerprint\": \"kzvdpFkKvgzbNgKU\",\n" + "          \"funding\": \"credit\",\n" +
                "          \"incremental_authorization\": {\n" + "            \"status\": \"unavailable\"\n" +
                "          },\n" + "          \"installments\": null,\n" + "          \"last4\": \"4242\",\n" +
                "          \"mandate\": null,\n" + "          \"multicapture\": {\n" +
                "            \"status\": \"unavailable\"\n" + "          },\n" + "          \"network\": \"visa\",\n" +
                "          \"network_token\": {\n" + "            \"used\": false\n" + "          },\n" +
                "          \"overcapture\": {\n" + "            \"maximum_amount_capturable\": 1000,\n" +
                "            \"status\": \"unavailable\"\n" + "          },\n" +
                "          \"three_d_secure\": null,\n" + "          \"wallet\": null\n" + "        },\n" +
                "        \"type\": \"card\"\n" + "      },\n" + "      \"receipt_email\": null,\n" +
                "      \"receipt_number\": null,\n" +
                "      \"receipt_url\": \"https://pay.stripe.com/receipts/payment/CAcaFwoVYWNjdF8xTzkzdktCNmFIR0FRVEdDKLCc3aoGMgYUY7Vtd8o6LBaODTXgIzTj3RMybaZ7bw8GrbZnUvJs1ltysOqF7kETjllU8Zu228YLOOn5\",\n" +
                "      \"refunded\": false,\n" + "      \"review\": null,\n" + "      \"shipping\": null,\n" +
                "      \"source\": null,\n" + "      \"source_transfer\": null,\n" +
                "      \"statement_descriptor\": null,\n" + "      \"statement_descriptor_suffix\": null,\n" +
                "      \"status\": \"succeeded\",\n" + "      \"transfer_data\": null,\n" +
                "      \"transfer_group\": null\n" + "    }\n" + "  },\n" + "  \"livemode\": false,\n" +
                "  \"pending_webhooks\": 2,\n" + "  \"request\": {\n" + "    \"id\": \"req_F8uHP66wV5Tddy\",\n" +
                "    \"idempotency_key\": \"3d9044f1-ef5f-4af6-ab37-66dd90a86816\"\n" + "  },\n" +
                "  \"type\": \"charge.succeeded\"\n" + "}";
        return body;
    }
    private String getRequestSignature(){
        String signature = "t=1700220464,v1=568ce3831b530347a261580d6b47c1794aefe54b70a9d12a6d359a0d53ac84af,v0=af1ab02ff14c9c243e2e62951a80fc904f4f9073ef32140c860f336d86cd7b62";
        return signature;
    }
}
