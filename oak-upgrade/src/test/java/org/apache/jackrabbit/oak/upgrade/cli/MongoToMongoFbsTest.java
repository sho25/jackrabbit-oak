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
import|import static
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
name|container
operator|.
name|MongoNodeStoreContainer
operator|.
name|isMongoAvailable
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|BlobStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|FileBlobStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|MongoNodeStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|NodeStoreContainer
import|;
end_import

begin_comment
comment|/**  * This covers the bug GRANITE-8719.  */
end_comment

begin_class
specifier|public
class|class
name|MongoToMongoFbsTest
extends|extends
name|AbstractOak2OakTest
block|{
specifier|private
specifier|final
name|BlobStoreContainer
name|destinationBlob
decl_stmt|;
specifier|private
specifier|final
name|NodeStoreContainer
name|source
decl_stmt|;
specifier|private
specifier|final
name|NodeStoreContainer
name|destination
decl_stmt|;
specifier|public
name|MongoToMongoFbsTest
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
name|isMongoAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|destinationBlob
operator|=
operator|new
name|FileBlobStoreContainer
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|MongoNodeStoreContainer
argument_list|()
expr_stmt|;
name|destination
operator|=
operator|new
name|MongoNodeStoreContainer
argument_list|(
name|destinationBlob
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStoreContainer
name|getSourceContainer
parameter_list|()
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStoreContainer
name|getDestinationContainer
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
index|[]
name|getArgs
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"--copy-binaries"
block|,
literal|"--fileblobstore"
block|,
name|destinationBlob
operator|.
name|getDescription
argument_list|()
block|,
name|source
operator|.
name|getDescription
argument_list|()
block|,
name|destination
operator|.
name|getDescription
argument_list|()
block|}
return|;
block|}
block|}
end_class

end_unit

