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
name|composite
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
name|PropertyUnbounded
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
name|CheckpointMBean
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
name|mount
operator|.
name|Mount
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
name|mount
operator|.
name|MountInfoProvider
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
name|ArrayList
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
name|HashSet
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CompositeNodeStoreService
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
name|CompositeNodeStoreService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GLOBAL_ROLE
init|=
literal|"composite:global"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MOUNT_ROLE_PREFIX
init|=
literal|"composite:mount:"
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
name|STATIC
argument_list|)
specifier|private
name|MountInfoProvider
name|mountInfoProvider
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|MANDATORY_MULTIPLE
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|,
name|bind
operator|=
literal|"bindNodeStore"
argument_list|,
name|unbind
operator|=
literal|"unbindNodeStore"
argument_list|,
name|referenceInterface
operator|=
name|NodeStoreProvider
operator|.
name|class
argument_list|,
name|target
operator|=
literal|"(!(service.pid=org.apache.jackrabbit.oak.composite.CompositeNodeStore))"
argument_list|)
specifier|private
name|List
argument_list|<
name|NodeStoreWithProps
argument_list|>
name|nodeStores
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Ignore read only writes"
argument_list|,
name|unbounded
operator|=
name|PropertyUnbounded
operator|.
name|ARRAY
argument_list|,
name|description
operator|=
literal|"Writes to these read-only paths won't fail the commit"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_IGNORE_READ_ONLY_WRITES
init|=
literal|"ignoreReadOnlyWrites"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Read-only mounts"
argument_list|,
name|description
operator|=
literal|"The partial stores should be configured as read-only"
argument_list|,
name|boolValue
operator|=
literal|true
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_PARTIAL_READ_ONLY
init|=
literal|"partialReadOnly"
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
name|checkpointReg
decl_stmt|;
specifier|private
name|ObserverTracker
name|observerTracker
decl_stmt|;
specifier|private
name|String
index|[]
name|ignoreReadOnlyWritePaths
decl_stmt|;
specifier|private
name|boolean
name|partialReadOnly
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
name|context
operator|=
name|context
expr_stmt|;
name|ignoreReadOnlyWritePaths
operator|=
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_IGNORE_READ_ONLY_WRITES
argument_list|)
argument_list|)
expr_stmt|;
name|partialReadOnly
operator|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_PARTIAL_READ_ONLY
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registerCompositeNodeStore
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
name|unregisterCompositeNodeStore
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|registerCompositeNodeStore
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
comment|// already registered
block|}
name|NodeStoreWithProps
name|globalNs
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|availableMounts
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeStoreWithProps
name|ns
range|:
name|nodeStores
control|)
block|{
if|if
condition|(
name|isGlobalNodeStore
argument_list|(
name|ns
argument_list|)
condition|)
block|{
name|globalNs
operator|=
name|ns
expr_stmt|;
block|}
else|else
block|{
name|availableMounts
operator|.
name|add
argument_list|(
name|getMountName
argument_list|(
name|ns
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|globalNs
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Composite node store registration is deferred until there's a global node store registered in OSGi"
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Found global node store: {}"
argument_list|,
name|getDescription
argument_list|(
name|globalNs
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Mount
name|m
range|:
name|mountInfoProvider
operator|.
name|getNonDefaultMounts
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|availableMounts
operator|.
name|contains
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Composite node store registration is deferred until there's mount {} registered in OSGi"
argument_list|,
name|m
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Node stores for all configured mounts are available"
argument_list|)
expr_stmt|;
name|CompositeNodeStore
operator|.
name|Builder
name|builder
init|=
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|mountInfoProvider
argument_list|,
name|globalNs
operator|.
name|getNodeStoreProvider
argument_list|()
operator|.
name|getNodeStore
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPartialReadOnly
argument_list|(
name|partialReadOnly
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|ignoreReadOnlyWritePaths
control|)
block|{
name|builder
operator|.
name|addIgnoredReadOnlyWritePath
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|NodeStoreWithProps
name|ns
range|:
name|nodeStores
control|)
block|{
if|if
condition|(
name|isGlobalNodeStore
argument_list|(
name|ns
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|mountName
init|=
name|getMountName
argument_list|(
name|ns
argument_list|)
decl_stmt|;
if|if
condition|(
name|mountName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|addMount
argument_list|(
name|mountName
argument_list|,
name|ns
operator|.
name|getNodeStoreProvider
argument_list|()
operator|.
name|getNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Mounting {} as {}"
argument_list|,
name|getDescription
argument_list|(
name|ns
argument_list|)
argument_list|,
name|mountName
argument_list|)
expr_stmt|;
block|}
block|}
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
name|CompositeNodeStore
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
literal|"nodeStoreType=compositeStore"
block|}
argument_list|)
expr_stmt|;
name|CompositeNodeStore
name|store
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|Whiteboard
name|whiteboard
init|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
operator|.
name|getBundleContext
argument_list|()
argument_list|)
decl_stmt|;
name|checkpointReg
operator|=
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CheckpointMBean
operator|.
name|class
argument_list|,
operator|new
name|CompositeCheckpointMBean
argument_list|(
name|store
argument_list|)
argument_list|,
name|CheckpointMBean
operator|.
name|TYPE
argument_list|,
literal|"Composite node store checkpoint management"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering the composite node store"
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
specifier|private
name|boolean
name|isGlobalNodeStore
parameter_list|(
name|NodeStoreWithProps
name|ns
parameter_list|)
block|{
return|return
name|GLOBAL_ROLE
operator|.
name|equals
argument_list|(
name|ns
operator|.
name|getRole
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getMountName
parameter_list|(
name|NodeStoreWithProps
name|ns
parameter_list|)
block|{
name|String
name|role
init|=
name|ns
operator|.
name|getRole
argument_list|()
decl_stmt|;
if|if
condition|(
name|role
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|role
operator|.
name|startsWith
argument_list|(
name|MOUNT_ROLE_PREFIX
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|role
operator|.
name|substring
argument_list|(
name|MOUNT_ROLE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getDescription
parameter_list|(
name|NodeStoreWithProps
name|ns
parameter_list|)
block|{
return|return
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|ns
operator|.
name|getProps
argument_list|()
operator|.
name|get
argument_list|(
literal|"oak.nodestore.description"
argument_list|)
argument_list|,
name|ns
operator|.
name|getNodeStoreProvider
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|unregisterCompositeNodeStore
parameter_list|()
block|{
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
literal|"Unregistering the composite node store"
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
if|if
condition|(
name|checkpointReg
operator|!=
literal|null
condition|)
block|{
name|checkpointReg
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|checkpointReg
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
block|}
specifier|protected
name|void
name|bindNodeStore
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
name|NodeStoreWithProps
name|newNs
init|=
operator|new
name|NodeStoreWithProps
argument_list|(
name|ns
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|nodeStores
operator|.
name|add
argument_list|(
name|newNs
argument_list|)
expr_stmt|;
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
literal|"bindNodeStore: context is null, delaying reconfiguration"
argument_list|)
expr_stmt|;
return|return;
block|}
name|unregisterCompositeNodeStore
argument_list|()
expr_stmt|;
name|registerCompositeNodeStore
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|unbindNodeStore
parameter_list|(
name|NodeStoreProvider
name|ns
parameter_list|)
block|{
name|Iterator
argument_list|<
name|NodeStoreWithProps
argument_list|>
name|it
init|=
name|nodeStores
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|getNodeStoreProvider
argument_list|()
operator|==
name|ns
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
literal|"unbindNodeStore: context is null, delaying reconfiguration"
argument_list|)
expr_stmt|;
return|return;
block|}
name|unregisterCompositeNodeStore
argument_list|()
expr_stmt|;
name|registerCompositeNodeStore
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|NodeStoreWithProps
block|{
specifier|private
specifier|final
name|NodeStoreProvider
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|props
decl_stmt|;
specifier|public
name|NodeStoreWithProps
parameter_list|(
name|NodeStoreProvider
name|nodeStore
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|props
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|props
operator|=
name|props
expr_stmt|;
block|}
specifier|public
name|NodeStoreProvider
name|getNodeStoreProvider
parameter_list|()
block|{
return|return
name|nodeStore
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getProps
parameter_list|()
block|{
return|return
name|props
return|;
block|}
specifier|public
name|String
name|getRole
parameter_list|()
block|{
return|return
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|NodeStoreProvider
operator|.
name|ROLE
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

