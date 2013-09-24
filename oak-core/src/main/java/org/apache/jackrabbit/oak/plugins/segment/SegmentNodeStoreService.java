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
name|segment
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|Mongo
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
name|Service
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
name|Blob
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
name|CommitFailedException
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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|plugins
operator|.
name|segment
operator|.
name|mongo
operator|.
name|MongoStore
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
name|CommitHook
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
name|PostCommitHook
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
name|NodeBuilder
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
name|NodeState
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
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
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
annotation|@
name|Service
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|)
specifier|public
class|class
name|SegmentNodeStoreService
implements|implements
name|NodeStore
block|{
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"The unique name of this instance"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"TarMK directory (if unset, use MongoDB)"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|DIRECTORY
init|=
literal|"repository.home"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"TarMK mode (64 for memory mapping, 32 for normal file access)"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|MODE
init|=
literal|"tarmk.mode"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"TarMK maximum file size"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|SIZE
init|=
literal|"tarmk.size"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"MongoDB host"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|HOST
init|=
literal|"host"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"MongoDB host"
argument_list|,
name|intValue
operator|=
literal|27017
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PORT
init|=
literal|"port"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"MongoDB database"
argument_list|,
name|value
operator|=
literal|"Oak"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|DB
init|=
literal|"db"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"Cache size (MB)"
argument_list|,
name|intValue
operator|=
literal|200
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|CACHE
init|=
literal|"cache"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Mongo
name|mongo
decl_stmt|;
specifier|private
name|SegmentStore
name|store
decl_stmt|;
specifier|private
name|NodeStore
name|delegate
decl_stmt|;
specifier|private
specifier|synchronized
name|NodeStore
name|getDelegate
parameter_list|()
block|{
assert|assert
name|delegate
operator|!=
literal|null
operator|:
literal|"service must be activated when used"
assert|;
return|return
name|delegate
return|;
block|}
annotation|@
name|Activate
specifier|public
specifier|synchronized
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Dictionary
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
init|=
name|context
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|name
operator|=
literal|""
operator|+
name|properties
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|String
name|host
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|HOST
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
name|String
name|directory
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|DIRECTORY
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
name|directory
operator|=
literal|"tarmk"
expr_stmt|;
block|}
name|String
name|mode
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|MODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|==
literal|null
condition|)
block|{
name|mode
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|MODE
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|,
literal|"32"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|size
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|null
condition|)
block|{
name|size
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|SIZE
argument_list|,
literal|"268435456"
argument_list|)
expr_stmt|;
comment|// 256MB
block|}
name|mongo
operator|=
literal|null
expr_stmt|;
name|store
operator|=
operator|new
name|FileStore
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|size
argument_list|)
argument_list|,
literal|"64"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|PORT
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|db
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|DB
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|cache
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|CACHE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|mongo
operator|=
operator|new
name|Mongo
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|SegmentCache
name|sc
init|=
operator|new
name|SegmentCache
argument_list|(
name|cache
operator|*
name|MB
argument_list|)
decl_stmt|;
name|store
operator|=
operator|new
name|MongoStore
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|(
name|db
argument_list|)
argument_list|,
name|sc
argument_list|)
expr_stmt|;
block|}
name|delegate
operator|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|lookup
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|String
name|property
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|property
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|property
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
if|if
condition|(
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Deactivate
specifier|public
specifier|synchronized
name|void
name|deactivate
parameter_list|()
block|{
name|delegate
operator|=
literal|null
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|mongo
operator|!=
literal|null
condition|)
block|{
name|mongo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//---------------------------------------------------------< NodeStore>--
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|commitHook
parameter_list|,
name|PostCommitHook
name|committed
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|commitHook
argument_list|,
name|committed
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|rebase
argument_list|(
name|builder
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|reset
argument_list|(
name|builder
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|createBlob
argument_list|(
name|stream
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|": "
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

