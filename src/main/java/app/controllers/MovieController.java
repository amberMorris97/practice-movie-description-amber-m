package app.controllers;


import app.dto.MovieDTO;
import app.models.Movie;
import app.repositories.MovieRepository;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    MovieRepository movieRepository;

    @GetMapping("")
    public ResponseEntity<?> getAllMovies() {
        List<Movie> allMovies = movieRepository.findAll();
        return new ResponseEntity<>(allMovies, HttpStatus.OK);
    }

    @GetMapping("/randomMovie")
    public Movie getRandomMovie() {
        List<Movie> allMovies = movieRepository.findAll();

        int randomIndex = (int) Math.floor(Math.random() * allMovies.size());

        return allMovies.get(randomIndex);
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMovie(@RequestBody MovieDTO movieData) throws HttpException, IOException {
        Client client = new Client();

        Movie movie = new Movie(movieData.getTitle(), movieData.getRating());

        String query = "Provide a 2-3 sentence plot summary for the movie: " + movie.getTitle()
                + "Return ONLY the text of the description. "
                + "Do NOT include introductory phrases, quotes, titles, or conversational text.";

        GenerateContentResponse response = client.models.generateContent("gemini-3.6-flash", query, null);

        movie.setDescription(response.text());

        movieRepository.save(movie);

        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }
}
