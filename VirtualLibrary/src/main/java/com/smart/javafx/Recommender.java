package com.smart.javafx;

import java.io.Serializable;
import java.util.*;

public class Recommender implements Serializable {

    private PriorityQueue<Book> recommendations;

    private HashMap<Book, Integer> borrowCount = new HashMap<>();

    public Recommender() {
        recommendations = new PriorityQueue<>(new BookComparator(borrowCount));
    }

    public void borrow(Book book) {
        // "book" is the Book object that the user borrowed
        borrowCount.put(book, borrowCount.getOrDefault(book, 0) + 1);
    }

    public void returnBook(Book book) {
        int count = borrowCount.get(book);
        borrowCount.remove(book);
        borrowCount.put(book, count);
    }

    public List<Book> getRecommendations() {
        recommendations.addAll(borrowCount.keySet());
        int numBooksToRecommend = 10; // The number of books you want to recommend
        List<Book> recommendedBooksList = new ArrayList<>();
        // Add maximum possible recommended books
        // If there are fewer books than numBooksToRecommend
        // all the books will be recommended
        for (int i = 0; i < numBooksToRecommend && !recommendations.isEmpty(); i++) {
            recommendedBooksList.add(recommendations.poll());
        }
        return recommendedBooksList;
    }

    class BookComparator implements Comparator<Book>, Serializable {

        private static final long serialVersionUID = 1L;

        private Map<Book, Integer> borrowCount;

        public BookComparator(Map<Book, Integer> borrowCount) {
            this.borrowCount = borrowCount;
        }

        @Override
        public int compare(Book book1, Book book2) {
            Integer count1 = borrowCount.getOrDefault(book1, 0);
            Integer count2 = borrowCount.getOrDefault(book2, 0);
            return count2.compareTo(count1);
        }
    }
}
