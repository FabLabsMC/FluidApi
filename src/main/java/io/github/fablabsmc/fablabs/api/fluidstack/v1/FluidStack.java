package io.github.fablabsmc.fablabs.api.fluidstack.v1;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.util.NbtType;

public final class FluidStack {
	public static final FluidStack EMPTY = new FluidStack((Fluid) null);

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
		amount = Fraction.fromTag(tag.getCompound("Amount"));

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
		amount = amount.add(incrementBy).simplify();
	}

	public void decrement(Fraction decrementBy) {
		amount = amount.subtract(decrementBy).simplify();
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

	public CompoundTag toTag(CompoundTag tag) {
		tag.putString("id", Registry.FLUID.getId(getFluid()).toString());

		if (this.tag != null) {
			tag.put("tag", this.tag.copy());
		}

		return tag;
	}
}
