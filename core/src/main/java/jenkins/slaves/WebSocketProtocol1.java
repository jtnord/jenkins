/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.slaves;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.UnprotectedRootAction;

//@ServerEndpoint("/websocket-slaves/v1/{agent-name}")
@Restricted(DoNotUse.class) // not a public API
public class WebSocketProtocol1 implements UnprotectedRootAction {

    /** Our logger */
    private final static Logger LOG = Logger.getLogger(WebSocketProtocol1.class.getName());

    @OnOpen
    public void onOpen(Session session, EndpointConfig epConfig,  @PathParam("agent-name") String agentName) {
        LOG.log(Level.INFO, "Agent ''{0}'' initiated a new connection", agentName);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason, @PathParam("agent-name") String agentName) {
        LOG.log(Level.INFO, "Agent ''{0}'' disconnected due to {1}", new Object[] {agentName, closeReason});
    }

    @OnError
    public void onError(Session session, Throwable t,  @PathParam("agent-name") String agentName) {
        LogRecord lr = new LogRecord(Level.WARNING, "Error occured on websocket for agent ''{0}''");
        lr.setParameters(new Object[] {agentName});
        lr.setThrown(t);
        LOG.log(lr);
        
        // TODO close the connection??
    }

    @OnMessage
    public void onPong(Session session, PongMessage pongMsg, @PathParam("agent-name") String agentName) {
        LOG.log(Level.INFO, "Agent ''{0}'' responded to a Ping", agentName);
        // TODO the client can add extra data in the Pong  - pongMsg.getApplicationData()
    }

    @OnMessage
    public void onTextData(Session session, String data, @PathParam("agent-name") String agentName) {
        LOG.log(Level.INFO, "Agent ''{0}'' send data - ''{1}''", new Object[] {agentName, data});
    }

    @OnMessage
    public void onBinaryData(Session session, byte[] data, @PathParam("agent-name") String agentName) {
        LOG.log(Level.INFO, "Agent ''{0}'' send binary data with lenght ''{1}''", new Object[] {agentName, data.length});
    }


    public void doDynamic(StaplerRequest request, StaplerResponse response) {
        LOG.log(Level.INFO, "doIndex {0}, {1}", new Object[] {request, response});
    }
    
    @Override
    public String getIconFileName() {
        // TODO Auto-generated method stub
        return "someicon.jpg";
    }

    @Override
    public String getDisplayName() {
        // TODO Auto-generated method stub
        return "a test";
    }

    @Override
    public String getUrlName() {
        // TODO Auto-generated method stub
        return "/websocket-slaves";
    }

}
