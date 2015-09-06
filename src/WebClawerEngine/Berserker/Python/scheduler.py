from TaskTable import TaskTable
from threading import Condition
import thread
"""
# called by each thread
def get_url(q, url):
    q.put(urllib2.urlopen(url).read())

theurls = ["http://google.com", "http://yahoo.com"]

q = Queue.Queue()

for u in theurls:
    t = threading.Thread(target=get_url, args = (q,u))
    t.daemon = True
    t.start()

s = q.get()
print s
"""

class scheduler:
    table = None
    id = 0
    table_lock = None
    condition = None

    def __init__(self):
        self.table = TaskTable()
        self.id = 0
        self.table_lock = thread.allocate_lock()
        self.condition = Condition()

    def add_task(self, link, title, type):
        task = {}
        task['link'] = link
        task['title'] = title
        task['type'] = type

        task['id'] = self.id
        self.id += 1
        task['status'] = "wait"
        self.table_lock.acquire()
        self.table.add_task(task)
        self.table_lock.release()


    def get_task(self):
        self.table_lock.acquire()
        task = self.table.get_pending_task()
        self.table_lock.release()
        return task

    def task_finish(self, task):
        self.table_lock.acquire()
        self.table.finish_task(task)
        self.table_lock.release()

