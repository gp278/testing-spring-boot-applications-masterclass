package de.rieckpil.courses.book.review;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;

import static de.rieckpil.courses.book.review.RandomReviewParameterResolverExtension.RandomReview;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomReviewParameterResolverExtension.class)
class ReviewVerifierTest {

  private ReviewVerifier reviewVerifier;

  @BeforeEach
  void setup() {
    reviewVerifier = new ReviewVerifier();
  }

  @Test
  void shouldFailWhenReviewContainsSwearWord() {
    String review = "This book is shit";
    System.out.println("Testing a review");

    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect swear word");
  }

  @Test
  @DisplayName("Should fail when review contains 'lorem ipsum'")
  void testLoremIpsum() {
    String review = "bla Lorem Ipsum Dolor";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect lorem ipsum");
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/badReview.csv")
  void shouldFailWhenReviewIsOfBadQuality(String review) {
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect bad review");
  }

  @RepeatedTest(5)
  void shouldFailWhenRandomReviewQualityIsBad(@RandomReview String review) {
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect bad review");
  }

  @Test
  void shouldPassWhenReviewIsGood() {
    String goodReview = "This is a good review. It has no swear words and enough words and so on.";
    boolean result = reviewVerifier.doesMeetQualityStandards(goodReview);
    assertTrue(result, "ReviewVerifier did not detect bad review");
  }

  @Test
  void shouldPassWhenReviewIsGoodHamcrest() {
    String goodReview = "This is a good review. It has no swear words and enough words and so on.";
    boolean result = reviewVerifier.doesMeetQualityStandards(goodReview);
    MatcherAssert.assertThat("ReviewVerifier did not detect bad review", result, Matchers.equalTo(true));
    MatcherAssert.assertThat("Lorem Ipsum", Matchers.endsWith("psum"));
    MatcherAssert.assertThat(List.of(1,2,3,4,5), Matchers.hasSize(5));
    MatcherAssert.assertThat(List.of(1,2,3,4,5), Matchers.anyOf(Matchers.hasSize(5), Matchers.emptyIterable()));
    MatcherAssert.assertThat(List.of(1,2,3,4,5), Matchers.allOf(Matchers.hasSize(5), Matchers.notNullValue()));
  }

  @Test
  void shouldPassWhenReviewIsGoodAssertJ() {
    String goodReview = "This is a good review. It has no swear words and enough words and so on.";
    boolean result = reviewVerifier.doesMeetQualityStandards(goodReview);
    Assertions.assertThat(result)
      .withFailMessage("ReviewVerifier did not detect bad review")
      .isEqualTo(true)
      .isTrue();

    Assertions.assertThat(List.of(1,2,3,4,5))
      .hasSizeBetween(2,10)
      .contains(3)
      .isNotEmpty()
    ;

  }
}
