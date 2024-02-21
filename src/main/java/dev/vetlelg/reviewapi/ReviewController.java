package dev.vetlelg.reviewapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    @Autowired
    private ReviewRepository repo;
    @Autowired
    private MongoTemplate temp;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Map<String, String> payload) {
        Review review = new Review(payload.get("reviewBody"));

        repo.insert(review);

        temp.update(Movie.class)
                .matching(Criteria.where("imdbId").is(payload.get("imdbId")))
                .apply(new Update().push("reviewIds").value(review))
                .first();

        return new ResponseEntity<Review>(review, HttpStatus.CREATED);
    }
}
