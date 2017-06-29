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
name|blob
operator|.
name|BlobReferenceRetriever
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
name|blob
operator|.
name|ReferenceCollector
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

begin_comment
comment|/**  * Implementation of {@link BlobReferenceRetriever} to retrieve blob references from the  * {@link SegmentTracker}.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentBlobReferenceRetriever
implements|implements
name|BlobReferenceRetriever
block|{
specifier|private
specifier|final
name|FileStore
name|store
decl_stmt|;
specifier|public
name|SegmentBlobReferenceRetriever
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
name|void
name|collectReferences
parameter_list|(
specifier|final
name|ReferenceCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|collectBlobReferences
argument_list|(
name|s
lambda|->
name|collector
operator|.
name|addReference
argument_list|(
name|s
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

