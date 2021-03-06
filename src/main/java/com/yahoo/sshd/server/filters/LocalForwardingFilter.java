/*
 * Copyright 2014 Yahoo! Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the License); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an AS IS BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.yahoo.sshd.server.filters;


import org.apache.sshd.common.Session;
import org.apache.sshd.common.SshdSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class exists to deny all sorts of forwarding.
 * 
 * we don't allow X11, agent, forwarding, or listening.
 * 
 * @author areese
 * 
 */
public class LocalForwardingFilter extends DenyingForwardingFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalForwardingFilter.class);
    private final String toHost;
    private final int toPort;

    public LocalForwardingFilter(String toHost, int toPort) {
        this.toHost = toHost.toLowerCase();
        this.toPort = toPort;
    }

    @Override
    public boolean canConnect(SshdSocketAddress socketAddress, Session session) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connect forwarding requested from {} for {}", socketAddress, session);
        }

        // we don't want to allow forwards to just anywhere, we only allow them to the artifactory host.
        int sp = socketAddress.getPort();
        if (sp != toPort) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Denying forwarding requested from {} for {} tohost {} toPort {}", socketAddress, session,
                                toHost, Integer.valueOf(toPort));
            }

            return false;
        }

        String spHost = socketAddress.getHostName().toLowerCase();
        if (!toHost.equals(spHost)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Denying forwarding requested from {} for {} tohost {} toPort {}", socketAddress, session,
                                toHost, Integer.valueOf(toPort));
            }

            return false;
        }

        return true;
    }
}
