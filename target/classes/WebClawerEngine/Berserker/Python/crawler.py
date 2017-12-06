import urllib2
import re
import threading
import time


class crawler(threading.Thread):
    scheduler = None

    def __init__(self, scheduler):
        super(crawler, self).__init__()
        self.scheduler = scheduler

    def crawl(self, task):
        if task == None:
            return

        if task["type"] == "list":
            self.crawl_list(task["link"])
        else:
            time.sleep(3)
            self.craw_page(task["link"], task["title"])

    def crawl_list(self, url):
        request = urllib2.Request(url)
        response = urllib2.urlopen(request)
        page = response.read()
        pattern = re.compile('<a href="http://news.ifeng.com/a/(.*?)" target="_blank" title="(.*?)">.*?</a>', re.S);
        items = re.findall(pattern, page)
        for item in items:
          self.scheduler.add_task("http://news.ifeng.com/a/" + item[0], item[1], "page")

    def craw_page(self, url, title):
        try:
            request = urllib2.Request(url)
            response = urllib2.urlopen(request)
            page = response.read()
            file = open("/Users/amaliujia/Documents/github/AssassinSE/webpages/" + title + ".html", 'wb')
            file.write(page)
            file.close()
        except (IOError, NameError, TypeError, urllib2.URLError) as e:
            print e

    def run(self):
        while True:
            task = self.scheduler.get_task()
            if task == None:
                continue
            task["status"] = "working"
            self.crawl(task)
            task["status"] = "done"
            self.scheduler.task_finish(task)
