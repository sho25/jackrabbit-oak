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
name|Nonnull
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
name|state
operator|.
name|AbstractNodeStore
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
name|NodeStoreBranch
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
name|com
operator|.
name|mongodb
operator|.
name|Mongo
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
extends|extends
name|AbstractNodeStore
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
literal|"directory"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"MongoDB host"
argument_list|,
name|value
operator|=
literal|"localhost"
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
name|long
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
if|if
condition|(
name|properties
operator|.
name|get
argument_list|(
name|DIRECTORY
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|directory
init|=
name|properties
operator|.
name|get
argument_list|(
name|DIRECTORY
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|mongo
operator|=
literal|null
expr_stmt|;
name|store
operator|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|host
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|HOST
argument_list|)
argument_list|)
decl_stmt|;
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
name|cache
operator|*
name|MB
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
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeStoreBranch
name|branch
parameter_list|()
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|branch
argument_list|()
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

