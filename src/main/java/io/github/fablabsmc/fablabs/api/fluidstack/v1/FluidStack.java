package io.github.fablabsmc.fablabs.api.fluidstack.v1;

import com.mojang.datafixers.Dynamic;

import net.minecraft.datafixer.NbtOps;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.util.NbtType;

//TODO: name not final, will likely become FluidVolume before PR
public final class FluidStack {
	public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY);

	private Fluid fluid;
	private Fraction amount;
	private CompoundTag tag;

	private boolean empty;

	public FluidStack(Fluid fluid) {
		this(fluid, Fraction.ONE);
	}

	public FluidStack(Fluid fluid, Fraction amount) {
		this.fluid = fluid;
		this.amount = amount;
		this.updateEmptyState();
	}

	private FluidStack(CompoundTag tag) {
		fluid = Registry.FLUID.get(new Identifier(tag.getString("id")));
		amount = Fraction.deserialize(new Dynamic<>(NbtOps.INSTANCE, tag.getCompound("Amount")));

		if (tag.contains("tag", NbtType.COMPOUND)) {
			this.tag = tag.getCompound("tag");
		}

		this.updateEmptyState();
	}

	public Fluid getFluid() {
		return empty ? Fluids.EMPTY : fluid;
	}

	public Fraction getAmount() {
		return empty ? Fraction.ZERO : amount;
	}

	public boolean isEmpty() {
		if (this == EMPTY) {
			return true;
		} else if (this.getFluid() != null && this.getFluid() != Fluids.EMPTY) {
			return !this.amount.isPositive();
		} else {
			return true;
		}
	}

	public void setAmount(Fraction amount) {
		this.amount = amount;
	}

	public void increment(Fraction incrementBy) {
		amount = amount.add(incrementBy);
	}

	public void decrement(Fraction decrementBy) {
		amount = amount.subtract(decrementBy);
		if (amount.isNegative()) amount = Fraction.ZERO;
	}

	//TODO: split and the like

	private void updateEmptyState() {
		empty = isEmpty();
	}

	public boolean hasTag() {
		return !empty && tag != null && !tag.isEmpty();
	}

	public CompoundTag getTag() {
		return tag;
	}

	public CompoundTag getOrCreateTag() {
		if (tag == null) {
			tag = new CompoundTag();
		}

		return tag;
	}

	public void setTag(CompoundTag tag) {
		this.tag = tag;
	}

	public FluidStack copy() {
		if (this.isEmpty()) return FluidStack.EMPTY;
		FluidStack stack = new FluidStack(this.fluid, this.amount);
		if (this.hasTag()) stack.setTag(this.getTag());
		return stack;
	}

	public static FluidStack fromTag(CompoundTag tag) {
		return new FluidStack(tag);
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putString("id", Registry.FLUID.getId(getFluid()).toString());

		tag.put("Amount", amount.serialize(NbtOps.INSTANCE));

		if (this.tag != null) {
			tag.put("tag", this.tag.copy());
		}

		return tag;
	}
}
