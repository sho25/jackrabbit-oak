begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|blob
operator|.
name|datastore
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|DataRecord
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
name|datastore
operator|.
name|directaccess
operator|.
name|DataRecordAccessProvider
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
name|datastore
operator|.
name|directaccess
operator|.
name|DataRecordDownloadOptions
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
name|datastore
operator|.
name|directaccess
operator|.
name|DataRecordUpload
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
name|BlobOptions
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
name|stats
operator|.
name|BlobStatsCollector
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
name|stats
operator|.
name|StatsCollectingStreams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Class used to test BlobStoreStats in {@link DataStoreBlobStoreStatsTest}.  *  * This class creates a data store that can generate delays in data store calls  * or generate errors from data store calls as needed.  This allows for more  * complete testing of metrics that are being measured in  * {@link DataStoreBlobStore}.  *  * Use the {@link BlobStoreStatsTestableFileDataStoreBuilder} to build an  * instance.  The provided "wither" methods should be used with the builder to  * specify which delay or error behaviors are needed, if any.  Then use the  * builder to build an {@link OakFileDataStore} subclass that behaves just like  * a regular OakFileDataStore but introduces the requested delays and/or errors  * for certain types of behaviors (e.g. reads, writes, etc.).  */
end_comment

begin_class
specifier|public
class|class
name|BlobStoreStatsTestableFileDataStore
extends|extends
name|OakFileDataStore
implements|implements
name|DataRecordAccessProvider
implements|,
name|TypedDataStore
block|{
specifier|private
name|int
name|readDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|writeDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|deleteDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|listDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|initUploadDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|completeUploadDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|getDownloadDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|withReadError
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|withWriteError
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|withDeleteError
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|withListError
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|withInitUploadError
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|withCompleteUploadError
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|withGetDownloadError
init|=
literal|false
decl_stmt|;
specifier|private
name|BlobStatsCollector
name|stats
init|=
literal|null
decl_stmt|;
specifier|private
name|DataStoreException
name|ex
init|=
operator|new
name|DataStoreException
argument_list|(
literal|"Test-generated Exception"
argument_list|)
decl_stmt|;
specifier|private
name|BlobStoreStatsTestableFileDataStore
parameter_list|(
name|int
name|readDelay
parameter_list|,
name|int
name|writeDelay
parameter_list|,
name|int
name|deleteDelay
parameter_list|,
name|int
name|listDelay
parameter_list|,
name|int
name|initUploadDelay
parameter_list|,
name|int
name|completeUploadDelay
parameter_list|,
name|int
name|getDownloadDelay
parameter_list|,
name|boolean
name|withReadError
parameter_list|,
name|boolean
name|withWriteError
parameter_list|,
name|boolean
name|withDeleteError
parameter_list|,
name|boolean
name|withListError
parameter_list|,
name|boolean
name|withInitUploadError
parameter_list|,
name|boolean
name|withCompleteUploadError
parameter_list|,
name|boolean
name|withGetDownloadError
parameter_list|,
name|BlobStatsCollector
name|stats
parameter_list|)
block|{
name|this
operator|.
name|readDelay
operator|=
name|readDelay
expr_stmt|;
name|this
operator|.
name|writeDelay
operator|=
name|writeDelay
expr_stmt|;
name|this
operator|.
name|deleteDelay
operator|=
name|deleteDelay
expr_stmt|;
name|this
operator|.
name|listDelay
operator|=
name|listDelay
expr_stmt|;
name|this
operator|.
name|initUploadDelay
operator|=
name|initUploadDelay
expr_stmt|;
name|this
operator|.
name|completeUploadDelay
operator|=
name|completeUploadDelay
expr_stmt|;
name|this
operator|.
name|getDownloadDelay
operator|=
name|getDownloadDelay
expr_stmt|;
name|this
operator|.
name|withReadError
operator|=
name|withReadError
expr_stmt|;
name|this
operator|.
name|withWriteError
operator|=
name|withWriteError
expr_stmt|;
name|this
operator|.
name|withDeleteError
operator|=
name|withDeleteError
expr_stmt|;
name|this
operator|.
name|withListError
operator|=
name|withListError
expr_stmt|;
name|this
operator|.
name|withInitUploadError
operator|=
name|withInitUploadError
expr_stmt|;
name|this
operator|.
name|withCompleteUploadError
operator|=
name|withCompleteUploadError
expr_stmt|;
name|this
operator|.
name|withGetDownloadError
operator|=
name|withGetDownloadError
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
block|}
specifier|public
specifier|static
name|BlobStoreStatsTestableFileDataStoreBuilder
name|getBuilder
parameter_list|()
block|{
return|return
operator|new
name|BlobStoreStatsTestableFileDataStoreBuilder
argument_list|()
return|;
block|}
specifier|public
specifier|static
class|class
name|BlobStoreStatsTestableFileDataStoreBuilder
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DELAY_DEFAULT
init|=
literal|50
decl_stmt|;
specifier|private
name|int
name|readDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|writeDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|deleteDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|listDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|initBlobUploadDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|completeBlobUploadDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|getDownloadURIDelay
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|generateErrorOnAddRecord
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateErrorOnGetRecord
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateErrorOnDeleteRecord
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateErrorOnListIds
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateErrorOnInitBlobUpload
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateErrorOnCompleteBlobUpload
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateErrorOnGetDownloadURI
init|=
literal|false
decl_stmt|;
specifier|private
name|BlobStatsCollector
name|stats
init|=
literal|null
decl_stmt|;
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withReadDelay
parameter_list|()
block|{
return|return
name|withReadDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withReadDelay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
name|readDelay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withWriteDelay
parameter_list|()
block|{
return|return
name|withWriteDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withWriteDelay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
name|writeDelay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withDeleteDelay
parameter_list|()
block|{
return|return
name|withDeleteDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withDeleteDelay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
name|deleteDelay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withListDelay
parameter_list|()
block|{
return|return
name|withListDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withListDelay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
name|listDelay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withInitBlobUploadDelay
parameter_list|()
block|{
return|return
name|withInitBlobUploadDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withInitBlobUploadDelay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
name|initBlobUploadDelay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withCompleteBlobUploadDelay
parameter_list|()
block|{
return|return
name|withCompleteBlobUploadDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withCompleteBlobUploadDelay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
name|completeBlobUploadDelay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withGetDownloadURIDelay
parameter_list|()
block|{
return|return
name|withGetDownloadURIDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withGetDownloadURIDelay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
name|getDownloadURIDelay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnGetRecord
parameter_list|()
block|{
return|return
name|withErrorOnGetRecord
argument_list|(
literal|true
argument_list|)
operator|.
name|withReadDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnGetRecord
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
name|generateErrorOnGetRecord
operator|=
name|withError
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnAddRecord
parameter_list|()
block|{
return|return
name|withErrorOnAddRecord
argument_list|(
literal|true
argument_list|)
operator|.
name|withWriteDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnAddRecord
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
name|generateErrorOnAddRecord
operator|=
name|withError
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnDeleteRecord
parameter_list|()
block|{
return|return
name|withErrorOnDeleteRecord
argument_list|(
literal|true
argument_list|)
operator|.
name|withDeleteDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnDeleteRecord
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
name|generateErrorOnDeleteRecord
operator|=
name|withError
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnList
parameter_list|()
block|{
return|return
name|withErrorOnList
argument_list|(
literal|true
argument_list|)
operator|.
name|withListDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnList
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
name|generateErrorOnListIds
operator|=
name|withError
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnInitBlobUpload
parameter_list|()
block|{
return|return
name|withErrorOnInitBlobUpload
argument_list|(
literal|true
argument_list|)
operator|.
name|withInitBlobUploadDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnInitBlobUpload
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
name|generateErrorOnInitBlobUpload
operator|=
name|withError
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnCompleteBlobUpload
parameter_list|()
block|{
return|return
name|withErrorOnCompleteBlobUpload
argument_list|(
literal|true
argument_list|)
operator|.
name|withCompleteBlobUploadDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnCompleteBlobUpload
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
name|generateErrorOnCompleteBlobUpload
operator|=
name|withError
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnGetDownloadURI
parameter_list|()
block|{
return|return
name|withErrorOnGetDownloadURI
argument_list|(
literal|true
argument_list|)
operator|.
name|withGetDownloadURIDelay
argument_list|(
name|DELAY_DEFAULT
argument_list|)
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withErrorOnGetDownloadURI
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
name|generateErrorOnGetDownloadURI
operator|=
name|withError
expr_stmt|;
return|return
name|this
return|;
block|}
name|BlobStoreStatsTestableFileDataStoreBuilder
name|withStatsCollector
parameter_list|(
name|BlobStatsCollector
name|stats
parameter_list|)
block|{
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
return|return
name|this
return|;
block|}
name|OakFileDataStore
name|build
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|==
name|readDelay
operator|&&
literal|0
operator|==
name|writeDelay
operator|&&
literal|0
operator|==
name|deleteDelay
operator|&&
literal|0
operator|==
name|listDelay
operator|&&
literal|0
operator|==
name|initBlobUploadDelay
operator|&&
literal|0
operator|==
name|completeBlobUploadDelay
operator|&&
literal|0
operator|==
name|getDownloadURIDelay
operator|&&
operator|!
name|generateErrorOnAddRecord
operator|&&
operator|!
name|generateErrorOnGetRecord
operator|&&
operator|!
name|generateErrorOnDeleteRecord
operator|&&
operator|!
name|generateErrorOnListIds
operator|&&
operator|!
name|generateErrorOnInitBlobUpload
operator|&&
operator|!
name|generateErrorOnCompleteBlobUpload
operator|&&
operator|!
name|generateErrorOnGetDownloadURI
operator|&&
literal|null
operator|==
name|stats
condition|)
block|{
return|return
operator|new
name|OakFileDataStore
argument_list|()
return|;
block|}
return|return
operator|new
name|BlobStoreStatsTestableFileDataStore
argument_list|(
name|readDelay
argument_list|,
name|writeDelay
argument_list|,
name|deleteDelay
argument_list|,
name|listDelay
argument_list|,
name|initBlobUploadDelay
argument_list|,
name|completeBlobUploadDelay
argument_list|,
name|getDownloadURIDelay
argument_list|,
name|generateErrorOnGetRecord
argument_list|,
name|generateErrorOnAddRecord
argument_list|,
name|generateErrorOnDeleteRecord
argument_list|,
name|generateErrorOnListIds
argument_list|,
name|generateErrorOnInitBlobUpload
argument_list|,
name|generateErrorOnCompleteBlobUpload
argument_list|,
name|generateErrorOnGetDownloadURI
argument_list|,
name|stats
argument_list|)
return|;
block|}
block|}
specifier|protected
name|void
name|delay
parameter_list|(
name|int
name|delay
parameter_list|)
block|{
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|void
name|err
parameter_list|(
name|boolean
name|withError
parameter_list|)
throws|throws
name|DataStoreException
block|{
if|if
condition|(
name|withError
condition|)
throw|throw
name|ex
throw|;
block|}
specifier|protected
name|void
name|forceErr
parameter_list|(
name|boolean
name|withError
parameter_list|)
block|{
if|if
condition|(
name|withError
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
specifier|public
specifier|static
class|class
name|ReadDelayedDataRecord
implements|implements
name|DataRecord
block|{
specifier|private
name|DataRecord
name|internalRecord
decl_stmt|;
specifier|private
name|BlobStatsCollector
name|stats
decl_stmt|;
specifier|private
name|long
name|startNanos
decl_stmt|;
specifier|private
name|ReadDelayedDataRecord
parameter_list|(
name|DataRecord
name|record
parameter_list|,
name|BlobStatsCollector
name|stats
parameter_list|,
name|long
name|startNanos
parameter_list|)
block|{
name|this
operator|.
name|internalRecord
operator|=
name|record
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|startNanos
operator|=
name|startNanos
expr_stmt|;
block|}
specifier|public
specifier|static
name|ReadDelayedDataRecord
name|wrap
parameter_list|(
name|DataRecord
name|record
parameter_list|,
name|BlobStatsCollector
name|stats
parameter_list|,
name|long
name|startNanos
parameter_list|)
block|{
return|return
operator|new
name|ReadDelayedDataRecord
argument_list|(
name|record
argument_list|,
name|stats
argument_list|,
name|startNanos
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DataIdentifier
name|getIdentifier
parameter_list|()
block|{
return|return
name|internalRecord
operator|.
name|getIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|()
block|{
return|return
name|internalRecord
operator|.
name|getReference
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|DataStoreException
block|{
return|return
name|internalRecord
operator|.
name|getLength
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|internalRecord
operator|.
name|getLastModified
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|DataStoreException
block|{
return|return
literal|null
operator|!=
name|stats
condition|?
name|StatsCollectingStreams
operator|.
name|wrap
argument_list|(
name|stats
argument_list|,
name|internalRecord
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|internalRecord
operator|.
name|getStream
argument_list|()
argument_list|,
name|startNanos
argument_list|)
else|:
name|internalRecord
operator|.
name|getStream
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|addRecord
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|writeDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withWriteError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|addRecord
argument_list|(
name|is
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|addRecord
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|BlobOptions
name|options
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|writeDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withWriteError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|addRecord
argument_list|(
name|is
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteRecord
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|deleteDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withDeleteError
argument_list|)
expr_stmt|;
name|super
operator|.
name|deleteRecord
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecord
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|readDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withReadError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getRecord
argument_list|(
name|identifier
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|deleteAllOlderThan
parameter_list|(
name|long
name|min
parameter_list|)
block|{
name|delay
argument_list|(
name|deleteDelay
argument_list|)
expr_stmt|;
name|forceErr
argument_list|(
name|withDeleteError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|deleteAllOlderThan
argument_list|(
name|min
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecordIfStored
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|delay
argument_list|(
name|readDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withReadError
argument_list|)
expr_stmt|;
return|return
name|ReadDelayedDataRecord
operator|.
name|wrap
argument_list|(
name|super
operator|.
name|getRecordIfStored
argument_list|(
name|identifier
argument_list|)
argument_list|,
name|stats
argument_list|,
name|start
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecordFromReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|readDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withReadError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getRecordFromReference
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecordForId
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|readDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withReadError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getRecordForId
argument_list|(
name|identifier
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DataRecord
argument_list|>
name|getAllRecords
parameter_list|()
block|{
name|delay
argument_list|(
name|listDelay
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getAllRecords
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DataIdentifier
argument_list|>
name|getAllIdentifiers
parameter_list|()
block|{
name|delay
argument_list|(
name|listDelay
argument_list|)
expr_stmt|;
name|forceErr
argument_list|(
name|withListError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getAllIdentifiers
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addMetadataRecord
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|writeDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withWriteError
argument_list|)
expr_stmt|;
name|super
operator|.
name|addMetadataRecord
argument_list|(
name|is
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addMetadataRecord
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|delay
argument_list|(
name|writeDelay
argument_list|)
expr_stmt|;
name|err
argument_list|(
name|withWriteError
argument_list|)
expr_stmt|;
name|super
operator|.
name|addMetadataRecord
argument_list|(
name|f
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|delay
argument_list|(
name|readDelay
argument_list|)
expr_stmt|;
name|forceErr
argument_list|(
name|withReadError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getMetadataRecord
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|metadataRecordExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|delay
argument_list|(
name|readDelay
argument_list|)
expr_stmt|;
name|forceErr
argument_list|(
name|withReadError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|metadataRecordExists
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|DataRecord
argument_list|>
name|getAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|delay
argument_list|(
name|listDelay
argument_list|)
expr_stmt|;
name|forceErr
argument_list|(
name|withListError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getAllMetadataRecords
argument_list|(
name|prefix
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|delay
argument_list|(
name|deleteDelay
argument_list|)
expr_stmt|;
name|forceErr
argument_list|(
name|withDeleteError
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|deleteMetadataRecord
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|delay
argument_list|(
name|deleteDelay
argument_list|)
expr_stmt|;
name|forceErr
argument_list|(
name|withDeleteError
argument_list|)
expr_stmt|;
name|super
operator|.
name|deleteAllMetadataRecords
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecordUpload
name|initiateDataRecordUpload
parameter_list|(
name|long
name|maxUploadSizeInBytes
parameter_list|,
name|int
name|maxNumberOfURIs
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|delay
argument_list|(
name|initUploadDelay
argument_list|)
expr_stmt|;
if|if
condition|(
name|withInitUploadError
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
return|return
operator|new
name|DataRecordUpload
argument_list|()
block|{
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|String
name|getUploadToken
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMinPartSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxPartSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|Collection
argument_list|<
name|URI
argument_list|>
name|getUploadURIs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|DataRecord
name|completeDataRecordUpload
parameter_list|(
name|String
name|uploadToken
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|delay
argument_list|(
name|completeUploadDelay
argument_list|)
expr_stmt|;
if|if
condition|(
name|withCompleteUploadError
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
return|return
name|InMemoryDataRecord
operator|.
name|getInstance
argument_list|(
literal|"fake record"
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|URI
name|getDownloadURI
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|,
name|DataRecordDownloadOptions
name|downloadOptions
parameter_list|)
block|{
name|delay
argument_list|(
name|getDownloadDelay
argument_list|)
expr_stmt|;
if|if
condition|(
name|withGetDownloadError
condition|)
return|return
literal|null
return|;
return|return
name|URI
operator|.
name|create
argument_list|(
literal|"https://jackrabbit.apache.org/oak/docs/index.html"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

