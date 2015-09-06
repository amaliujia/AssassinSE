from TaskTable import TaskTable
from threading import Condition
import thread

class scheduler:
    table = None
    id = 0
    table_lock = None
    condition = None

    def __init__(self):
        self.table = TaskTable()
        self.id = 0
        self.condition = Condition()
        self.table_lock = thread.allocate_lock()



    def add_task(self, link, title, type):
        task = {}
        task['link'] = link
        task['title'] = title
        task['type'] = type

        task['id'] = self.id
        self.id += 1
        task['status'] = "wait"

        self.condition.acquire()
        self.table.add_task(task)
        self.condition.notify()
        self.condition.release()


    def get_task(self):
        self.condition.acquire()
        task = None
        while True:
            task = self.table.get_pending_task()
            if task == None:
                self.condition.wait()
            else:
                self.condition.release()
                break;
        return task

    def task_finish(self, task):
        self.table_lock.acquire()
        self.table.finish_task(task)
        self.table_lock.release()

