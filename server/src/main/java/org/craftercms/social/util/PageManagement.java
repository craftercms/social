package org.craftercms.social.util;

public class PageManagement {
   
   private static final long serialVersionUID = 112218218182L;

   private static final int PAGE_SIZE = 50;
   private static final int DEFAULT_START = 0;
   private static final int DEFAULT_END = 50;
   private static final int DEFAULT_TOTAL = 50;

   private int start;
   private int end;

   private int pageSize;

   private long total;
   
   public PageManagement() {
	   this.pageSize = PAGE_SIZE;
	   this.start = DEFAULT_START;
	   this.end = DEFAULT_END;
	   this.total = DEFAULT_TOTAL;
   }

   public int getStart() {
       return start;
   }

   public void setStart(int start) {
       this.start = start;
   }

   public int getEnd() {
       return end;
   }

   public void setEnd(int end) {
       this.end = end;
   }

   public int getPageSize() {
       return pageSize;
   }

   public void setPageSize(int pageSize) {
       this.pageSize = pageSize;
       this.start = 0;
       this.end = pageSize - 1;
   }

   public void next() {
       if ((end + pageSize) <= (total - 1)) {
           end += pageSize;
           start += pageSize;
       } else if (end < (total - 1)) {
           end = (int)total - 1;
           start += pageSize;
       }
   }
   
   public boolean isLastPage() {
	   return getCurrentPage() == getTotalPages();
   }
   
   private int getCurrentPage() {
	   int c = (end + 1) / pageSize;
	   if (((end + 1) % pageSize) > 0) {
		   c ++;
	   }
	   return c;
   }
   
   private int getTotalPages() {
	   int pages =  (int) (total / pageSize);
	   if ((total % pageSize) > 0) {
		   pages ++;
	   }
	   return pages;
   }

   public void previous() {
       if (end == (total - 1)) {
           end = start - 1;
           start -= pageSize;

       } else if ((start - pageSize) > 0) {
           end -= pageSize;
           start -= pageSize;
       } else if (start > 0) {
           start = 0;
           end = pageSize - 1;
       }
   }

   public long getTotal() {
       return total;
   }

   public void setTotal(long total) {
       this.total = total;
       this.start = 0;
       if ((this.total < pageSize)) {
           this.end = (int)total;
       } else {
           this.end = pageSize - 1;
       }
   }

   

}
