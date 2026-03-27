package com.mycompany.library_system.utility;

import com.mycompany.library_system.adt.MapInterface;

/**
 *
 * @author all
 */
public class Pagination<K, V> {
    private int currentPage = 1;
    private int totalPages = 1;
    private final int PAGE_SIZE = 30;

    private boolean isAscending = true;
    private String searchingWord = "";

    private MapInterface<K, V> displayTree;

    public void setDisplayTree(MapInterface<K, V> tree) {
        this.displayTree = tree;
        recalculateTotalPages();
        currentPage = 1;
    }

    public MapInterface<K, V> getDisplayTree() {
        return displayTree;
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
        if (displayTree == null || displayTree.isEmpty()) {
            totalPages = 1;
            return;
        }

        totalPages = (int) Math.ceil((double) displayTree.size() / PAGE_SIZE);
    }

    public int getStartIndex() {
        return (currentPage - 1) * PAGE_SIZE;
    }

    public int getEndIndex() {
        return Math.min(getStartIndex() + PAGE_SIZE - 1, displayTree.size() - 1);
    }
}
