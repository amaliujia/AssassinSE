
class TaskTable:
    past_tasks = []
    pending_tasks = []

    def __init__(self):
        self.past_tasks = []
        self.pending_tasks = []

    def add_task(self, task):
        self.pending_tasks.append(task)

    def finish_task(self, task):
        self.past_tasks.append(task)

    def get_pending_task(self):
        if len(self.pending_tasks) > 0:
            return self.pending_tasks.pop()
        else:
            return None
