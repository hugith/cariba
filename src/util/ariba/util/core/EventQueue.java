/*
    Copyright 1996-2008 Ariba, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    $Id: //ariba/platform/util/core/ariba/util/core/EventQueue.java#5 $
*/

package ariba.util.core;

/**
    A simple queue of events that can be added to and waited on by
    multiple threads

    @aribaapi private
*/
public class EventQueue
{
    private final Deque events = new Deque();

    protected boolean stopRunning;


    public int queueLength ()
    {
        synchronized (events) {
            return (events.count());
        }
    }
    /**
        Places <b>anEvent</b> on the event queue.
    */
    public void addEvent (EventProcessor anEvent)
    {
        synchronized (events) {
            events.enqueue(anEvent);
            events.notify();
        }
    }

    /**
        Removes and returns the next EventProcessor from the
        EventLoop's event queue.
    */
    public EventProcessor getNextEvent ()
    {
        EventProcessor nextEvent = null;

        synchronized (events) {
            while (events.isEmpty() && !stopRunning) {
                try {
                    events.wait(Date.MillisPerSecond * 10);
                }
                catch (InterruptedException e) {
                }
            }
            // we could leave the loop due to stopRunning
            if (!events.isEmpty()) {
                nextEvent = (EventProcessor)events.dequeue();
            }
        }
        return nextEvent;
    }

    public EventProcessor getNextEventIfAny ()
    {
        synchronized (events) {
            if (events.isEmpty()) {
                return null;
            }
            return((EventProcessor)events.dequeue());
        }
    }
}
