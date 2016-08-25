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
name|standby
operator|.
name|client
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
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|api
operator|.
name|Type
operator|.
name|BINARY
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
name|PropertyState
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
name|Type
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
name|memory
operator|.
name|EmptyNodeState
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
name|RecordId
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
name|SegmentBlob
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
name|SegmentNodeState
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
name|standby
operator|.
name|store
operator|.
name|RemoteSegmentLoader
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
name|standby
operator|.
name|store
operator|.
name|StandbyStore
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
name|NodeStateDiff
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
name|StandbyApplyDiff
implements|implements
name|NodeStateDiff
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
name|StandbyApplyDiff
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|StandbyStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|hasDataStore
decl_stmt|;
specifier|private
specifier|final
name|RemoteSegmentLoader
name|loader
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
comment|/**      * read-only traversal of the diff that has 2 properties: one is to log all      * the content changes, second is to drill down to properly level, so that      * missing binaries can be sync'ed if needed      */
specifier|private
specifier|final
name|boolean
name|logOnly
decl_stmt|;
specifier|public
name|StandbyApplyDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|StandbyStore
name|store
parameter_list|,
name|RemoteSegmentLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|builder
argument_list|,
name|store
argument_list|,
name|loader
argument_list|,
literal|"/"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|StandbyApplyDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|StandbyStore
name|store
parameter_list|,
name|RemoteSegmentLoader
name|loader
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|logOnly
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|hasDataStore
operator|=
name|store
operator|.
name|getBlobStore
argument_list|()
operator|!=
literal|null
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|logOnly
operator|=
name|logOnly
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loader
operator|.
name|isRunning
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|logOnly
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|binaryCheck
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|binaryCheck
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loader
operator|.
name|isRunning
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|logOnly
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|binaryCheck
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|binaryCheck
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loader
operator|.
name|isRunning
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|logOnly
condition|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|PropertyState
name|binaryCheck
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|BINARY
condition|)
block|{
name|binaryCheck
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|BINARIES
condition|)
block|{
for|for
control|(
name|Blob
name|blob
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
name|binaryCheck
argument_list|(
name|blob
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|property
return|;
block|}
specifier|private
name|void
name|binaryCheck
parameter_list|(
name|Blob
name|b
parameter_list|,
name|String
name|pName
parameter_list|)
block|{
if|if
condition|(
name|b
operator|instanceof
name|SegmentBlob
condition|)
block|{
name|SegmentBlob
name|sb
init|=
operator|(
name|SegmentBlob
operator|)
name|b
decl_stmt|;
comment|// verify if the blob exists
if|if
condition|(
name|sb
operator|.
name|isExternal
argument_list|()
operator|&&
name|hasDataStore
operator|&&
name|b
operator|.
name|getReference
argument_list|()
operator|==
literal|null
condition|)
block|{
name|String
name|blobId
init|=
name|sb
operator|.
name|getBlobId
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobId
operator|!=
literal|null
condition|)
block|{
name|readBlob
argument_list|(
name|blobId
argument_list|,
name|pName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown Blob {} at {}, ignoring"
argument_list|,
name|b
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|path
operator|+
literal|"#"
operator|+
name|pName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|String
name|pName
parameter_list|)
block|{
name|Blob
name|read
init|=
name|loader
operator|.
name|readBlob
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|store
operator|.
name|getBlobStore
argument_list|()
operator|.
name|writeBlob
argument_list|(
name|read
operator|.
name|getNewStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|f
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to persist blob "
operator|+
name|blobId
operator|+
literal|" at "
operator|+
name|path
operator|+
literal|"#"
operator|+
name|pName
argument_list|,
name|f
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to load remote blob "
operator|+
name|blobId
operator|+
literal|" at "
operator|+
name|path
operator|+
literal|"#"
operator|+
name|pName
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|process
argument_list|(
name|name
argument_list|,
literal|"childNodeAdded"
argument_list|,
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|process
argument_list|(
name|name
argument_list|,
literal|"childNodeChanged"
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|process
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|op
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loader
operator|.
name|isRunning
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|after
operator|instanceof
name|SegmentNodeState
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"{} {}, readonly binary check {}"
argument_list|,
name|op
argument_list|,
name|path
operator|+
name|name
argument_list|,
name|logOnly
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|logOnly
condition|)
block|{
name|RecordId
name|id
init|=
operator|(
operator|(
name|SegmentNodeState
operator|)
name|after
operator|)
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|store
operator|.
name|newSegmentNodeState
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"checkpoints"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// if we're on the /checkpoints path, there's no need for a deep
comment|// traversal to verify binaries
return|return
literal|true
return|;
block|}
if|if
condition|(
name|hasDataStore
condition|)
block|{
comment|// has external datastore, we need a deep
comment|// traversal to verify binaries
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|StandbyApplyDiff
argument_list|(
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|store
argument_list|,
name|loader
argument_list|,
name|path
operator|+
name|name
operator|+
literal|"/"
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loader
operator|.
name|isRunning
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|log
operator|.
name|trace
argument_list|(
literal|"childNodeDeleted {}, RO:{}"
argument_list|,
name|path
operator|+
name|name
argument_list|,
name|logOnly
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|logOnly
condition|)
block|{
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

