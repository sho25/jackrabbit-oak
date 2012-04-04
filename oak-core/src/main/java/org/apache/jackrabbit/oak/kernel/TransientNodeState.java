begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|kernel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|ChildNodeEntry
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
name|mk
operator|.
name|model
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
name|mk
operator|.
name|model
operator|.
name|PropertyState
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
specifier|public
class|class
name|TransientNodeState
block|{
specifier|private
specifier|final
name|KernelNodeStateEditor
name|editor
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|persistentState
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|NodeState
argument_list|,
name|TransientNodeState
argument_list|>
name|existingChildNodes
init|=
operator|new
name|HashMap
argument_list|<
name|NodeState
argument_list|,
name|TransientNodeState
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TransientNodeState
argument_list|>
name|addedNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TransientNodeState
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|removedNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|addedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|removedProperties
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|TransientNodeState
name|parent
decl_stmt|;
name|TransientNodeState
parameter_list|(
name|NodeState
name|persistentState
parameter_list|,
name|KernelNodeStateEditor
name|editor
parameter_list|,
name|TransientNodeState
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|editor
operator|=
name|editor
expr_stmt|;
name|this
operator|.
name|persistentState
operator|=
name|persistentState
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|private
name|TransientNodeState
parameter_list|(
name|KernelNodeStateEditor
name|parentEditor
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|parentEditor
argument_list|,
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TransientNodeState
parameter_list|(
name|KernelNodeStateEditor
name|parentEditor
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|persistedState
parameter_list|)
block|{
name|editor
operator|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|parentEditor
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|persistentState
operator|=
name|persistedState
expr_stmt|;
name|parent
operator|=
name|parentEditor
operator|.
name|getTransientState
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|private
name|TransientNodeState
parameter_list|(
name|TransientNodeState
name|state
parameter_list|,
name|TransientNodeState
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|editor
operator|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|parent
operator|.
name|getEditor
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|persistentState
operator|=
name|state
operator|.
name|persistentState
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|TransientNodeState
argument_list|>
name|added
range|:
name|addedNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|addedName
init|=
name|added
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|this
operator|.
name|addedNodes
operator|.
name|put
argument_list|(
name|addedName
argument_list|,
operator|new
name|TransientNodeState
argument_list|(
name|added
operator|.
name|getValue
argument_list|()
argument_list|,
name|this
argument_list|,
name|addedName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|removedNodes
operator|.
name|addAll
argument_list|(
name|state
operator|.
name|removedNodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|addedProperties
operator|.
name|putAll
argument_list|(
name|state
operator|.
name|addedProperties
argument_list|)
expr_stmt|;
name|this
operator|.
name|removedProperties
operator|.
name|addAll
argument_list|(
name|state
operator|.
name|removedProperties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|state
init|=
name|addedProperties
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
return|return
name|state
return|;
block|}
return|return
name|removedProperties
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|||
name|persistentState
operator|==
literal|null
condition|?
literal|null
else|:
name|persistentState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|TransientNodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|TransientNodeState
name|state
init|=
name|addedNodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
return|return
name|state
return|;
block|}
return|return
name|removedNodes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|?
literal|null
else|:
name|getExistingChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|removed
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|removed
operator|.
name|addAll
argument_list|(
name|removedProperties
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|added
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
argument_list|()
decl_stmt|;
name|added
operator|.
name|putAll
argument_list|(
name|addedProperties
argument_list|)
expr_stmt|;
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|persistedProperties
init|=
name|persistentState
operator|.
name|getProperties
argument_list|()
decl_stmt|;
comment|// fixme check for null
return|return
operator|new
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|PropertyState
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
init|=
name|persistedProperties
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|PropertyState
name|next
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
while|while
condition|(
name|properties
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PropertyState
name|prop
init|=
name|properties
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|added
operator|.
name|containsKey
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|next
operator|=
name|added
operator|.
name|get
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|removed
operator|.
name|contains
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|next
operator|=
name|prop
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|next
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|PropertyState
name|e
init|=
name|next
decl_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
return|return
name|e
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|TransientNodeState
argument_list|>
name|getChildNodes
parameter_list|(
name|long
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|removed
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|removed
operator|.
name|addAll
argument_list|(
name|removedNodes
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TransientNodeState
argument_list|>
name|added
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TransientNodeState
argument_list|>
argument_list|()
decl_stmt|;
name|added
operator|.
name|putAll
argument_list|(
name|addedNodes
argument_list|)
expr_stmt|;
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|persistedNodes
init|=
name|persistentState
operator|.
name|getChildNodeEntries
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
decl_stmt|;
comment|// fixme check for null
return|return
operator|new
name|Iterable
argument_list|<
name|TransientNodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|TransientNodeState
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|TransientNodeState
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|nodes
init|=
name|persistedNodes
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|TransientNodeState
name|next
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
while|while
condition|(
name|nodes
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|ChildNodeEntry
name|entry
init|=
name|nodes
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|added
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|next
operator|=
name|added
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|removed
operator|.
name|contains
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|next
operator|=
name|getExistingChildNode
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|next
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|TransientNodeState
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|TransientNodeState
name|e
init|=
name|next
decl_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
return|return
name|e
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|//------------------------------------------------------------< internal>---
name|KernelNodeStateEditor
name|getEditor
parameter_list|()
block|{
return|return
name|editor
return|;
block|}
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
else|else
block|{
name|String
name|path
init|=
name|parent
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|path
operator|.
name|isEmpty
argument_list|()
condition|?
name|name
else|:
name|path
operator|+
literal|'/'
operator|+
name|name
return|;
block|}
block|}
name|void
name|addNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|addedNodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|TransientNodeState
argument_list|(
name|editor
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|addedNodes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|removedNodes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
name|addedProperties
operator|.
name|put
argument_list|(
name|state
operator|.
name|getName
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|addedProperties
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|removedProperties
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|void
name|move
parameter_list|(
name|String
name|name
parameter_list|,
name|TransientNodeState
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|TransientNodeState
name|state
init|=
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|state
operator|.
name|name
operator|=
name|destName
expr_stmt|;
name|state
operator|.
name|parent
operator|=
name|destParent
expr_stmt|;
name|destParent
operator|.
name|addedNodes
operator|.
name|put
argument_list|(
name|destName
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
name|void
name|copy
parameter_list|(
name|String
name|name
parameter_list|,
name|TransientNodeState
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|destParent
operator|.
name|addedNodes
operator|.
name|put
argument_list|(
name|destName
argument_list|,
operator|new
name|TransientNodeState
argument_list|(
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|destParent
argument_list|,
name|destName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TransientNodeState
name|getExistingChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|persistentState
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NodeState
name|state
init|=
name|persistentState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|existingChildNodes
operator|.
name|get
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|transientState
operator|==
literal|null
condition|)
block|{
name|transientState
operator|=
operator|new
name|TransientNodeState
argument_list|(
name|editor
argument_list|,
name|name
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|existingChildNodes
operator|.
name|put
argument_list|(
name|state
argument_list|,
name|transientState
argument_list|)
expr_stmt|;
block|}
return|return
name|transientState
return|;
block|}
block|}
end_class

end_unit

