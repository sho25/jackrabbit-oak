begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|cow
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferenceCardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|jmx
operator|.
name|CopyOnWriteStoreMBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PropertiesUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|osgi
operator|.
name|OsgiWhiteboard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|commit
operator|.
name|ObserverTracker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStoreProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|Registration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|Whiteboard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardExecutor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|registerMBean
import|;
end_import

begin_class
annotation|@
name|Component
argument_list|(
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
specifier|public
class|class
name|COWNodeStoreService
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|COWNodeStoreService
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"NodeStoreProvider role"
argument_list|,
name|description
operator|=
literal|"Property indicating that this component will not register as a NodeStore but as a NodeStoreProvider with given role"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PROP_ROLE
init|=
literal|"role"
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|MANDATORY_UNARY
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|,
name|target
operator|=
literal|"(role=copy-on-write)"
argument_list|,
name|bind
operator|=
literal|"bindNodeStoreProvider"
argument_list|,
name|unbind
operator|=
literal|"unbindNodeStoreProvider"
argument_list|)
specifier|private
name|NodeStoreProvider
name|nodeStoreProvider
decl_stmt|;
specifier|private
name|String
name|nodeStoreDescription
decl_stmt|;
specifier|private
name|ComponentContext
name|context
decl_stmt|;
specifier|private
name|ServiceRegistration
name|nsReg
decl_stmt|;
specifier|private
name|Registration
name|mbeanReg
decl_stmt|;
specifier|private
name|ObserverTracker
name|observerTracker
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|WhiteboardExecutor
name|executor
decl_stmt|;
specifier|private
name|String
name|role
decl_stmt|;
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|this
operator|.
name|role
operator|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_ROLE
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|registerNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|protected
name|void
name|deactivate
parameter_list|()
block|{
name|unregisterNodeStore
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|registerNodeStore
parameter_list|()
block|{
if|if
condition|(
name|nsReg
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|nodeStoreProvider
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for the NodeStoreProvider with role=copy-on-write"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for the component activation"
argument_list|)
expr_stmt|;
return|return;
block|}
name|COWNodeStore
name|store
init|=
operator|new
name|COWNodeStore
argument_list|(
name|nodeStoreProvider
operator|.
name|getNodeStore
argument_list|()
argument_list|)
decl_stmt|;
name|whiteboard
operator|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|executor
operator|=
operator|new
name|WhiteboardExecutor
argument_list|()
expr_stmt|;
name|executor
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|mbeanReg
operator|=
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CopyOnWriteStoreMBean
operator|.
name|class
argument_list|,
name|store
operator|.
expr|new
name|MBeanImpl
argument_list|()
argument_list|,
name|CopyOnWriteStoreMBean
operator|.
name|TYPE
argument_list|,
literal|"Copy-on-write: "
operator|+
name|nodeStoreDescription
argument_list|)
expr_stmt|;
name|Dictionary
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Constants
operator|.
name|SERVICE_PID
argument_list|,
name|COWNodeStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"oak.nodestore.description"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"nodeStoreType=cowStore"
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|role
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering the COW node store"
argument_list|)
expr_stmt|;
name|observerTracker
operator|=
operator|new
name|ObserverTracker
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|observerTracker
operator|.
name|start
argument_list|(
name|context
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|nsReg
operator|=
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
operator|new
name|String
index|[]
block|{
name|NodeStore
operator|.
name|class
operator|.
name|getName
argument_list|()
block|}
argument_list|,
name|store
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering the COW node store provider"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"role"
argument_list|,
name|role
argument_list|)
expr_stmt|;
name|nsReg
operator|=
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
operator|new
name|String
index|[]
block|{
name|NodeStoreProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
block|}
argument_list|,
call|(
name|NodeStoreProvider
call|)
argument_list|()
operator|->
name|store
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|unregisterNodeStore
parameter_list|()
block|{
if|if
condition|(
name|mbeanReg
operator|!=
literal|null
condition|)
block|{
name|mbeanReg
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|mbeanReg
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|executor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|executor
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|observerTracker
operator|!=
literal|null
condition|)
block|{
name|observerTracker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|observerTracker
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|nsReg
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unregistering the COW node store"
argument_list|)
expr_stmt|;
name|nsReg
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|nsReg
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|bindNodeStoreProvider
parameter_list|(
name|NodeStoreProvider
name|ns
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|this
operator|.
name|nodeStoreProvider
operator|=
name|ns
expr_stmt|;
name|this
operator|.
name|nodeStoreDescription
operator|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"oak.nodestore.description"
argument_list|)
argument_list|,
name|ns
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|registerNodeStore
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|unbindNodeStoreProvider
parameter_list|(
name|NodeStoreProvider
name|ns
parameter_list|)
block|{
name|this
operator|.
name|nodeStoreProvider
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|nodeStoreDescription
operator|=
literal|null
expr_stmt|;
name|unregisterNodeStore
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

