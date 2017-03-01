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
name|segment
operator|.
name|standby
operator|.
name|server
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
name|standby
operator|.
name|server
operator|.
name|FileStoreUtil
operator|.
name|readSegmentWithRetry
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
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|file
operator|.
name|FileStore
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

begin_class
class|class
name|DefaultStandbySegmentReader
implements|implements
name|StandbySegmentReader
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DefaultStandbySegmentReader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|FileStore
name|store
decl_stmt|;
name|DefaultStandbySegmentReader
parameter_list|(
name|FileStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|readSegment
parameter_list|(
name|String
name|segmentId
parameter_list|)
block|{
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|fromString
argument_list|(
name|segmentId
argument_list|)
decl_stmt|;
name|long
name|msb
init|=
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|Segment
name|segment
init|=
name|readSegmentWithRetry
argument_list|(
name|store
argument_list|,
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
init|(
name|ByteArrayOutputStream
name|stream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
init|)
block|{
name|segment
operator|.
name|writeTo
argument_list|(
name|stream
argument_list|)
expr_stmt|;
return|return
name|stream
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error while reading segment content"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

