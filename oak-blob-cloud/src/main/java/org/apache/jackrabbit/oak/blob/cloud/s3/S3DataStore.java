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
name|blob
operator|.
name|cloud
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|Strings
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
name|core
operator|.
name|data
operator|.
name|DataIdentifier
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
name|core
operator|.
name|data
operator|.
name|DataStoreException
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
name|AbstractSharedCachingDataStore
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
name|SharedBackend
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

begin_comment
comment|/**  * Amazon S3 data store extending from {@link AbstractSharedCachingDataStore}.  */
end_comment

begin_class
specifier|public
class|class
name|S3DataStore
extends|extends
name|AbstractSharedCachingDataStore
block|{
comment|/**      * Logger instance.      */
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
name|S3DataStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Properties
name|properties
decl_stmt|;
comment|/**      * The minimum size of an object that should be stored in this data store.      */
specifier|private
name|int
name|minRecordLength
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
specifier|private
name|String
name|secret
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|SharedBackend
name|createBackend
parameter_list|()
block|{
name|S3Backend
name|backend
init|=
operator|new
name|S3Backend
argument_list|()
decl_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
name|backend
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
return|return
name|backend
return|;
block|}
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|getOrCreateReferenceKey
parameter_list|()
throws|throws
name|DataStoreException
block|{
try|try
block|{
return|return
name|secret
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error in creating reference key"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Look in the backend for a record matching the given identifier.  Returns true      * if such a record exists.      *      * @param identifier - An identifier for the record.      * @return true if a record for the provided identifier can be found.      */
specifier|public
name|boolean
name|haveRecordForIdentifier
parameter_list|(
specifier|final
name|String
name|identifier
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
return|return
name|backend
operator|.
name|exists
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
name|identifier
argument_list|)
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|DataStoreException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Data Store Exception caught checking for %s in pending uploads"
argument_list|,
name|identifier
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**------------------------------------------- Getters& Setters-----------------------------**/
comment|/**      * Properties required to configure the S3Backend      */
specifier|public
name|void
name|setProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
specifier|public
name|SharedBackend
name|getBackend
parameter_list|()
block|{
return|return
name|backend
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMinRecordLength
parameter_list|()
block|{
return|return
name|minRecordLength
return|;
block|}
specifier|public
name|void
name|setMinRecordLength
parameter_list|(
name|int
name|minRecordLength
parameter_list|)
block|{
name|this
operator|.
name|minRecordLength
operator|=
name|minRecordLength
expr_stmt|;
block|}
specifier|public
name|void
name|setSecret
parameter_list|(
name|String
name|secret
parameter_list|)
block|{
name|this
operator|.
name|secret
operator|=
name|secret
expr_stmt|;
block|}
block|}
end_class

end_unit

