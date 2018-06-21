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
name|plugins
operator|.
name|blob
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|nanoTime
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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|commons
operator|.
name|jmx
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|formatTime
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
name|commons
operator|.
name|jmx
operator|.
name|ManagementOperation
operator|.
name|done
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
name|commons
operator|.
name|jmx
operator|.
name|ManagementOperation
operator|.
name|newManagementOperation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
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
name|jmx
operator|.
name|AnnotatedStandardMBean
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
name|jmx
operator|.
name|ManagementOperation
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
comment|/**  * Default implementation of {@link BlobGCMBean} based on a {@link BlobGarbageCollector}.  */
end_comment

begin_class
specifier|public
class|class
name|BlobGC
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|BlobGCMBean
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
name|BlobGC
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OP_NAME
init|=
literal|"Blob garbage collection"
decl_stmt|;
specifier|private
specifier|final
name|BlobGarbageCollector
name|blobGarbageCollector
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
name|ManagementOperation
argument_list|<
name|String
argument_list|>
name|gcOp
init|=
name|done
argument_list|(
name|OP_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONSISTENCY_OP_NAME
init|=
literal|"Blob consistency check"
decl_stmt|;
specifier|private
name|ManagementOperation
argument_list|<
name|String
argument_list|>
name|consistencyOp
init|=
name|done
argument_list|(
name|CONSISTENCY_OP_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/**      * @param blobGarbageCollector  Blob garbage collector      * @param executor              executor for running the garbage collection task      */
specifier|public
name|BlobGC
parameter_list|(
annotation|@
name|Nonnull
name|BlobGarbageCollector
name|blobGarbageCollector
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|)
block|{
name|super
argument_list|(
name|BlobGCMBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobGarbageCollector
operator|=
name|checkNotNull
argument_list|(
name|blobGarbageCollector
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|checkNotNull
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|startBlobGC
parameter_list|(
specifier|final
name|boolean
name|markOnly
parameter_list|)
block|{
if|if
condition|(
name|gcOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|gcOp
operator|=
name|newManagementOperation
argument_list|(
name|OP_NAME
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|nanoTime
argument_list|()
decl_stmt|;
name|blobGarbageCollector
operator|.
name|collectGarbage
argument_list|(
name|markOnly
argument_list|)
expr_stmt|;
return|return
literal|"Blob gc completed in "
operator|+
name|formatTime
argument_list|(
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|gcOp
argument_list|)
expr_stmt|;
block|}
return|return
name|getBlobGCStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|startBlobGC
parameter_list|(
specifier|final
name|boolean
name|markOnly
parameter_list|,
specifier|final
name|boolean
name|forceBlobIdRetrieve
parameter_list|)
block|{
if|if
condition|(
name|gcOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|gcOp
operator|=
name|newManagementOperation
argument_list|(
name|OP_NAME
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|nanoTime
argument_list|()
decl_stmt|;
name|blobGarbageCollector
operator|.
name|collectGarbage
argument_list|(
name|markOnly
argument_list|,
name|forceBlobIdRetrieve
argument_list|)
expr_stmt|;
return|return
literal|"Blob gc completed in "
operator|+
name|formatTime
argument_list|(
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|gcOp
argument_list|)
expr_stmt|;
block|}
return|return
name|getBlobGCStatus
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|getBlobGCStatus
parameter_list|()
block|{
return|return
name|gcOp
operator|.
name|getStatus
argument_list|()
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|checkConsistency
parameter_list|()
block|{
if|if
condition|(
name|consistencyOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|consistencyOp
operator|=
name|newManagementOperation
argument_list|(
name|CONSISTENCY_OP_NAME
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|missing
init|=
name|blobGarbageCollector
operator|.
name|checkConsistency
argument_list|()
decl_stmt|;
return|return
literal|"Consistency check completed in "
operator|+
name|formatTime
argument_list|(
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
operator|+
literal|". "
operator|+
operator|+
name|missing
operator|+
literal|" missing blobs found (details in the log)."
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|consistencyOp
argument_list|)
expr_stmt|;
block|}
return|return
name|getConsistencyCheckStatus
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|getConsistencyCheckStatus
parameter_list|()
block|{
return|return
name|consistencyOp
operator|.
name|getStatus
argument_list|()
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getGlobalMarkStats
parameter_list|()
block|{
name|TabularDataSupport
name|tds
decl_stmt|;
try|try
block|{
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
name|BlobGC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Garbage collection global mark phase Stats"
argument_list|,
name|MARK_TYPE
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"repositoryId"
block|}
argument_list|)
decl_stmt|;
name|tds
operator|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|GarbageCollectionRepoStats
argument_list|>
name|stats
init|=
name|blobGarbageCollector
operator|.
name|getStats
argument_list|()
decl_stmt|;
for|for
control|(
name|GarbageCollectionRepoStats
name|stat
range|:
name|stats
control|)
block|{
name|tds
operator|.
name|put
argument_list|(
name|toCompositeData
argument_list|(
name|stat
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|tds
return|;
block|}
specifier|private
name|CompositeDataSupport
name|toCompositeData
parameter_list|(
name|GarbageCollectionRepoStats
name|statObj
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[]
block|{
name|statObj
operator|.
name|getRepositoryId
argument_list|()
operator|+
operator|(
name|statObj
operator|.
name|isLocal
argument_list|()
condition|?
literal|" *"
else|:
literal|""
operator|)
block|,
operator|(
name|statObj
operator|.
name|getStartTime
argument_list|()
operator|==
literal|0
condition|?
literal|""
else|:
operator|(
operator|new
name|Date
argument_list|(
name|statObj
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|)
operator|)
operator|.
name|toString
argument_list|()
block|,
operator|(
name|statObj
operator|.
name|getEndTime
argument_list|()
operator|==
literal|0
condition|?
literal|""
else|:
operator|(
operator|new
name|Date
argument_list|(
name|statObj
operator|.
name|getEndTime
argument_list|()
argument_list|)
operator|)
operator|)
operator|.
name|toString
argument_list|()
block|,
name|statObj
operator|.
name|getLength
argument_list|()
block|,
name|humanReadableByteCount
argument_list|(
name|statObj
operator|.
name|getLength
argument_list|()
argument_list|)
block|,
name|statObj
operator|.
name|getNumLines
argument_list|()
block|}
decl_stmt|;
return|return
operator|new
name|CompositeDataSupport
argument_list|(
name|MARK_TYPE
argument_list|,
name|MARK_FIELD_NAMES
argument_list|,
name|values
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|MARK_FIELD_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"repositoryId"
block|,
literal|"markStartTime"
block|,
literal|"markEndTime"
block|,
literal|"referenceFileSizeBytes"
block|,
literal|"referencesFileSize"
block|,
literal|"numReferences"
block|,     }
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|MARK_FIELD_DESCRIPTIONS
init|=
operator|new
name|String
index|[]
block|{
literal|"Repository ID"
block|,
literal|"Start time of mark"
block|,
literal|"End time of mark"
block|,
literal|"References file size in bytes"
block|,
literal|"References file size in human readable format"
block|,
literal|"Number of references"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|OpenType
index|[]
name|MARK_FIELD_TYPES
init|=
operator|new
name|OpenType
index|[]
block|{
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|INTEGER
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CompositeType
name|MARK_TYPE
init|=
name|createMarkCompositeType
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|CompositeType
name|createMarkCompositeType
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|CompositeType
argument_list|(
name|GarbageCollectionRepoStats
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Composite data type for datastore GC statistics"
argument_list|,
name|MARK_FIELD_NAMES
argument_list|,
name|MARK_FIELD_DESCRIPTIONS
argument_list|,
name|MARK_FIELD_TYPES
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getOperationStats
parameter_list|()
block|{
name|TabularDataSupport
name|tds
decl_stmt|;
try|try
block|{
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
name|BlobGC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Garbage Collection Operation Stats"
argument_list|,
name|OP_STATS_TYPE
argument_list|,
name|OP_STATS_FIELD_NAMES
argument_list|)
decl_stmt|;
name|tds
operator|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
expr_stmt|;
name|OperationsStatsMBean
name|operationStats
init|=
name|blobGarbageCollector
operator|.
name|getOperationStats
argument_list|()
decl_stmt|;
name|tds
operator|.
name|put
argument_list|(
name|toCompositeData
argument_list|(
name|operationStats
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|tds
return|;
block|}
specifier|private
name|CompositeData
name|toCompositeData
parameter_list|(
name|OperationsStatsMBean
name|statObj
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[]
block|{
name|statObj
operator|.
name|getStartCount
argument_list|()
block|,
name|statObj
operator|.
name|getFailureCount
argument_list|()
block|,
name|statObj
operator|.
name|duration
argument_list|()
block|}
decl_stmt|;
return|return
operator|new
name|CompositeDataSupport
argument_list|(
name|OP_STATS_TYPE
argument_list|,
name|OP_STATS_FIELD_NAMES
argument_list|,
name|values
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|OP_STATS_FIELD_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"count"
block|,
literal|"failureCount"
block|,
literal|"duration"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|OP_STATS_FIELD_DESCRIPTIONS
init|=
operator|new
name|String
index|[]
block|{
literal|"Count"
block|,
literal|"Failure Count"
block|,
literal|"Duration"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|OpenType
index|[]
name|OP_STATS_FIELD_TYPES
init|=
operator|new
name|OpenType
index|[]
block|{
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CompositeType
name|OP_STATS_TYPE
init|=
name|createOpStatsCompositeType
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|CompositeType
name|createOpStatsCompositeType
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|CompositeType
argument_list|(
name|GarbageCollectionRepoStats
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Composite data type for datastore GC operation stats"
argument_list|,
name|OP_STATS_FIELD_NAMES
argument_list|,
name|OP_STATS_FIELD_DESCRIPTIONS
argument_list|,
name|OP_STATS_FIELD_TYPES
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

