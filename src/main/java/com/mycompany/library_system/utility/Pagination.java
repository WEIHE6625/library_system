package com.mycompany.library_system.utility;

import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.entity.Book;

public class Pagination<T> {
    private int currentPage = 1;
    private int totalPages = 1;
    private final int PAGE_SIZE = 30;

    private boolean isAscending = true;
    private String searchingWord = "";

    private ListInterface<T> displayList;

    public void setDisplayList(ListInterface<T> list) {
        this.displayList = list;
        recalculateTotalPages();
        currentPage = 1;
    }

    public ListInterface<T> getDisplayList() {
        return displayList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public void setIsAscending(boolean ascending) {
        this.isAscending = ascending;
    }

    public String getSearchingWord() {
        return searchingWord;
    }

    public void setSearchingWord(String word) {
        this.searchingWord = "Searching : " + word;
    }

    public void nextPage() {
        currentPage = (currentPage == totalPages) ? 1 : currentPage + 1;
    }

    public void previousPage() {
        currentPage = (currentPage == 1) ? totalPages : currentPage - 1;
    }

    private void recalculateTotalPages() {
        if (displayList == null || displayList.isEmpty()) {
            totalPages = 1;
            return;
        }

        totalPages = (int) Math.ceil((double) displayList.size() / PAGE_SIZE);
    }

    public int getStartIndex() {
        return (currentPage - 1) * PAGE_SIZE;
    }

    public int getEndIndex() {
        return Math.min(getStartIndex() + PAGE_SIZE - 1, displayList.size() - 1);
    }
}
