__author__ = 'amaliujia'

from scheduler import scheduler
from crawler import crawler

def add_crawler(crawlers, scheduler):
    crawlers.append(crawler(scheduler))

if __name__=="__main__":
    s = scheduler()
    crawlers = []
    add_crawler(crawlers, s)
    add_crawler(crawlers, s)
    add_crawler(crawlers, s)
    add_crawler(crawlers, s)
    add_crawler(crawlers, s)
    add_crawler(crawlers, s)

    s.add_task("http://news.ifeng.com/", "feng_news", "list")

    for crawler in crawlers:
       crawler.start()
