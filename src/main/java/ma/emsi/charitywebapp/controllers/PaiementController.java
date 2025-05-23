package ma.emsi.charitywebapp.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.services.DonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paiement")
public class PaiementController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final DonService donService;

    @Autowired
    public PaiementController(DonService donService) {
        this.donService = donService;
    }

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestParam double montant, @RequestParam String email)
            throws StripeException {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/user/dons?success=true")
                .setCancelUrl("http://localhost:8080/user/dons?canceled=true")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount((long) (montant * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Don caritatif")
                                                                .build())
                                                .build())
                                .build())
                .setCustomerEmail(email)
                .build();

        Session session = Session.create(params);

        // Crée et sauvegarde le don avec le stripeSessionId
        Don don = new Don();
        don.setMontant(montant);
        don.setStripeSessionId(session.getId());
        don.setStatutPaiement("EN_ATTENTE");
        // Ajoute ici les autres champs nécessaires (email, donateur, action...)
        donService.saveDon(don);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("id", session.getId());
        responseData.put("url", session.getUrl());
        return responseData;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {
        String payload = "";
        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            payload = sb.toString();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("");
        }

        String sigHeader = request.getHeader("Stripe-Signature");
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                Don don = donService.getDonByStripeSessionId(session.getId());
                if (don != null) {
                    don.setStatutPaiement("PAYE");
                    donService.saveDon(don);
                }
            }
        }
        return ResponseEntity.ok("");
    }
}
