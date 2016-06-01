begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|segment
operator|.
name|http
package|;
end_package

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
name|segment
operator|.
name|CachingSegmentReader
operator|.
name|DEFAULT_STRING_CACHE_MB
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
name|segment
operator|.
name|SegmentVersion
operator|.
name|LATEST_VERSION
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
name|segment
operator|.
name|SegmentWriters
operator|.
name|pooledSegmentWriter
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Suppliers
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|Revisions
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
name|SegmentNotFoundException
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
name|SegmentReader
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
name|SegmentReaders
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
name|SegmentStore
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
name|SegmentTracker
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
name|SegmentWriter
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
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_class
specifier|public
class|class
name|HttpStore
implements|implements
name|SegmentStore
block|{
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentTracker
name|tracker
init|=
operator|new
name|SegmentTracker
argument_list|(
name|this
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|HttpStoreRevisions
name|revisions
init|=
operator|new
name|HttpStoreRevisions
argument_list|(
name|this
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|segmentReader
init|=
name|SegmentReaders
operator|.
name|segmentReader
argument_list|(
name|this
argument_list|,
name|DEFAULT_STRING_CACHE_MB
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentWriter
name|segmentWriter
init|=
name|pooledSegmentWriter
argument_list|(
name|this
argument_list|,
name|LATEST_VERSION
argument_list|,
literal|"sys"
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|URL
name|base
decl_stmt|;
comment|/**      * @param base      *            make sure the url ends with a slash "/", otherwise the      *            requests will end up as absolute instead of relative      */
specifier|public
name|HttpStore
parameter_list|(
name|URL
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentTracker
name|getTracker
parameter_list|()
block|{
return|return
name|tracker
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentWriter
name|getWriter
parameter_list|()
block|{
return|return
name|segmentWriter
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentReader
name|getReader
parameter_list|()
block|{
return|return
name|segmentReader
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Revisions
name|getRevisions
parameter_list|()
block|{
return|return
name|revisions
return|;
block|}
comment|/**      * Builds a simple URLConnection. This method can be extended to add      * authorization headers if needed.      *       */
name|URLConnection
name|get
parameter_list|(
name|String
name|fragment
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|IOException
block|{
specifier|final
name|URL
name|url
decl_stmt|;
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
name|url
operator|=
name|base
expr_stmt|;
block|}
else|else
block|{
name|url
operator|=
operator|new
name|URL
argument_list|(
name|base
argument_list|,
name|fragment
argument_list|)
expr_stmt|;
block|}
return|return
name|url
operator|.
name|openConnection
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// FIXME OAK-4396: HttpStore.containsSegment throws SNFE instead of returning false for non existing segments
specifier|public
name|boolean
name|containsSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|sameStore
argument_list|(
name|this
argument_list|)
operator|||
name|readSegment
argument_list|(
name|id
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Segment
name|readSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
try|try
block|{
name|URLConnection
name|connection
init|=
name|get
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|stream
argument_list|)
decl_stmt|;
return|return
operator|new
name|Segment
argument_list|(
name|tracker
argument_list|,
name|segmentReader
argument_list|,
name|id
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SegmentNotFoundException
argument_list|(
name|id
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SegmentNotFoundException
argument_list|(
name|id
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|URLConnection
name|connection
init|=
name|get
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|.
name|setDoInput
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OutputStream
name|stream
init|=
name|connection
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|CheckForNull
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

