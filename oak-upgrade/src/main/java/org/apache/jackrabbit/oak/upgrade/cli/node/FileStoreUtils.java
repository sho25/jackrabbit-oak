begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|upgrade
operator|.
name|cli
operator|.
name|node
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|RecordType
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
name|segment
operator|.
name|Segment
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
name|segment
operator|.
name|SegmentId
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
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|file
operator|.
name|ReadOnlyFileStore
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
name|ProxyNodeStore
import|;
end_import

begin_class
specifier|public
class|class
name|FileStoreUtils
block|{
specifier|private
name|FileStoreUtils
parameter_list|()
block|{      }
specifier|public
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|ReadOnlyFileStore
name|fs
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|FileStore
name|fs
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|boolean
name|hasExternalBlobReferences
parameter_list|(
name|ReadOnlyFileStore
name|fs
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|SegmentId
name|id
range|:
name|fs
operator|.
name|getSegmentIds
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|id
operator|.
name|isDataSegmentId
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|forEachRecord
argument_list|(
operator|new
name|Segment
operator|.
name|RecordConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|consume
parameter_list|(
name|int
name|number
parameter_list|,
name|RecordType
name|type
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
comment|// FIXME the consumer should allow to stop processing
comment|// see java.nio.file.FileVisitor
if|if
condition|(
name|type
operator|==
name|RecordType
operator|.
name|BLOB_ID
condition|)
block|{
throw|throw
operator|new
name|ExternalBlobFound
argument_list|()
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|ExternalBlobFound
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ExternalBlobFound
extends|extends
name|RuntimeException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|NodeStoreWithFileStore
extends|extends
name|ProxyNodeStore
block|{
specifier|private
specifier|final
name|SegmentNodeStore
name|segmentNodeStore
decl_stmt|;
specifier|private
specifier|final
name|FileStore
name|fileStore
decl_stmt|;
specifier|public
name|NodeStoreWithFileStore
parameter_list|(
name|SegmentNodeStore
name|segmentNodeStore
parameter_list|,
name|FileStore
name|fileStore
parameter_list|)
block|{
name|this
operator|.
name|segmentNodeStore
operator|=
name|segmentNodeStore
expr_stmt|;
name|this
operator|.
name|fileStore
operator|=
name|fileStore
expr_stmt|;
block|}
specifier|public
name|FileStore
name|getFileStore
parameter_list|()
block|{
return|return
name|fileStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|SegmentNodeStore
name|getNodeStore
parameter_list|()
block|{
return|return
name|segmentNodeStore
return|;
block|}
block|}
block|}
end_class

end_unit

